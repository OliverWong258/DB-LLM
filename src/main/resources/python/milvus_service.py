from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
from transformers import BertTokenizer, BertModel
import torch
from pymilvus import connections, FieldSchema, CollectionSchema, DataType, Collection, utility
import time
import sys
import io

# 设置标准输出和输入为 UTF-8 编码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stdin = io.TextIOWrapper(sys.stdin.buffer, encoding='utf-8')

app = FastAPI()

# 定义请求的数据模型

# 插入操作的数据模型
class TextToInsert(BaseModel):
    id: int
    text: str

class InsertRequest(BaseModel):
    collection_name: str
    texts: List[TextToInsert]

# 查询操作的数据模型
class SearchRequest(BaseModel):
    collection_name: str
    query_text: str
    top_k: Optional[int] = 5  # 返回的结果数量，默认为5

# 定义搜索结果的数据模型
class SearchResult(BaseModel):
    id: int
    text: str
    distance: float

class SearchResponse(BaseModel):
    results: List[SearchResult]

# 全局变量，用于存储模型和Milvus集合
tokenizer = None
model = None
collections = {}

def initialize_milvus():
    global tokenizer, model, collections

    # 初始化 tokenizer 和模型
    tm = time.time()
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertModel.from_pretrained('bert-base-chinese')
    print("BERT模型加载时间:", time.time() - tm, "秒")

    tm = time.time()
    # 连接到 Milvus 服务器
    connections.connect("default", host="localhost", port="19530")
    print("已连接到 Milvus 服务器。")

    # 定义要加载的集合名称
    collection_names = ["policies", "cases"]

    for collection_name in collection_names:
        tm = time.time()
        if not utility.has_collection(collection_name):
            # 如果集合不存在，创建集合
            print(f"集合 '{collection_name}' 不存在，正在创建...")
            fields = [
                FieldSchema(name="id", dtype=DataType.INT64, is_primary=True),
                FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=768),
                FieldSchema(name="text", dtype=DataType.VARCHAR, max_length=65535)  # VARCHAR 类型用于存储文本
            ]
            schema = CollectionSchema(fields, description=f"BERT sentence embeddings for {collection_name}")
            collection = Collection(collection_name, schema)
            print(f"集合 '{collection_name}' 创建成功。")
        else:
            # 如果集合存在，获取集合
            collection = Collection(collection_name)
            print(f"集合 '{collection_name}' 已存在。")

        # 创建索引（如果尚未创建）
        if not collection.has_index():
            print(f"集合 '{collection_name}' 尚未创建索引，开始创建索引...")
            index_params = {
                "index_type": "IVF_FLAT",
                "metric_type": "L2",
                "params": {"nlist": 128}
            }
            collection.create_index(field_name="embedding", index_params=index_params)
            print(f"集合 '{collection_name}' 的索引创建成功。")
        else:
            print(f"集合 '{collection_name}' 已存在索引。")

        # 加载集合
        try:
            collection.load()
            print(f"集合 '{collection_name}' 加载成功，耗时 {time.time() - tm} 秒。")
            collections[collection_name] = collection
        except Exception as e:
            print(f"加载集合 '{collection_name}' 失败: {e}", file=sys.stderr)
            sys.exit(1)

def generate_embedding(text: str):
    # 使用 tokenizer 将文本转换为模型需要的格式
    inputs = tokenizer(text, return_tensors="pt", max_length=512, truncation=True, padding="max_length")

    # 将 tokenizer 的输出传递给模型，获取嵌入向量
    with torch.no_grad():
        outputs = model(**inputs)

    # 获取最后一层的隐藏状态
    last_hidden_states = outputs.last_hidden_state

    # 取第一个 token（[CLS] token）的嵌入作为整个句子的嵌入
    sentence_embedding = last_hidden_states[:, 0, :]

    # 将嵌入向量转换为一维的 numpy 数组
    sentence_embedding_np = sentence_embedding.squeeze().numpy()

    return sentence_embedding_np.tolist()

@app.on_event("startup")
def startup_event():
    initialize_milvus()

@app.post("/milvus")
def handle_request(operation: str, collection_name: str, request: Optional[BaseModel] = None):
    """
    通用接口，根据操作类型执行插入或查询操作。

    - **operation**: "insert" 或 "search"
    - **collection_name**: 集合名称，例如 "policies" 或 "cases"
    - **request**: 根据操作类型不同，传入不同的数据
    """
    if collection_name not in collections:
        raise HTTPException(status_code=400, detail=f"集合 '{collection_name}' 不存在。")

    collection = collections[collection_name]

    if operation == "insert":
        if not isinstance(request, InsertRequest):
            raise HTTPException(status_code=400, detail="插入操作需要 InsertRequest 数据。")
        insert_request: InsertRequest = request

        try:
            ids = []
            embeddings = []
            texts = []
            for item in insert_request.texts:
                ids.append(item.id)
                embeddings.append(generate_embedding(item.text))
                texts.append(item.text)
            
            entities = [
                ids,
                embeddings,
                texts
            ]

            # 插入数据
            insert_result = collection.insert(entities)
            collection.flush()  # 确保数据被持久化

            return {"status": "success", "insert_count": len(ids)}
        except Exception as e:
            raise HTTPException(status_code=500, detail=str(e))

    elif operation == "search":
        if not isinstance(request, SearchRequest):
            raise HTTPException(status_code=400, detail="查询操作需要 SearchRequest 数据。")
        search_request: SearchRequest = request

        try:
            query_embedding = generate_embedding(search_request.query_text)

            # 定义搜索参数
            search_params = {
                "metric_type": "L2",  # 使用的距离度量
                "params": {"nprobe": 10}  # 搜索参数
            }

            # 执行搜索
            results = collection.search(
                data=[query_embedding],          # 查询向量
                anns_field="embedding",          # 搜索的字段
                param=search_params,
                limit=search_request.top_k,      # 返回的结果数量
                expr=None,                       # 可选的过滤条件
                output_fields=["id", "text"]     # 要返回的字段
            )

            # 组织搜索结果
            search_results = []
            for hits in results:
                for hit in hits:
                    search_results.append(SearchResult(
                        id=hit.entity.get('id'),
                        text=hit.entity.get('text'),
                        distance=hit.distance
                    ))

            return SearchResponse(results=search_results)
        except Exception as e:
            raise HTTPException(status_code=500, detail=str(e))
    else:
        raise HTTPException(status_code=400, detail="无效的操作类型。应为 'insert' 或 'search'。")

# 定义路由模型，确保请求体正确解析
from fastapi import Depends
from fastapi.routing import APIRoute

@app.post("/milvus/insert")
def insert_texts(request: InsertRequest):
    """
    插入文本到指定的Milvus集合。
    """
    try:
        collection_name = request.collection_name
        if collection_name not in collections:
            raise HTTPException(status_code=400, detail=f"集合 '{collection_name}' 不存在。")

        collection = collections[collection_name]

        ids = []
        embeddings = []
        texts = []
        for item in request.texts:
            ids.append(item.id)
            embeddings.append(generate_embedding(item.text))
            texts.append(item.text)
        
        entities = [
            ids,
            embeddings,
            texts
        ]

        # 插入数据
        insert_result = collection.insert(entities)
        collection.flush()  # 确保数据被持久化

        return {"status": "success", "insert_count": len(ids)}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/milvus/search", response_model=SearchResponse)
def search_texts(request: SearchRequest):
    """
    在指定的Milvus集合中搜索最相似的文本。
    """
    try:
        collection_name = request.collection_name
        if collection_name not in collections:
            raise HTTPException(status_code=400, detail=f"集合 '{collection_name}' 不存在。")

        collection = collections[collection_name]

        query_embedding = generate_embedding(request.query_text)

        # 定义搜索参数
        search_params = {
            "metric_type": "L2",  # 使用的距离度量
            "params": {"nprobe": 10}  # 搜索参数
        }

        # 执行搜索
        results = collection.search(
            data=[query_embedding],          # 查询向量
            anns_field="embedding",          # 搜索的字段
            param=search_params,
            limit=request.top_k,             # 返回的结果数量
            expr=None,                       # 可选的过滤条件
            output_fields=["id", "text"]     # 要返回的字段
        )

        # 组织搜索结果
        search_results = []
        for hits in results:
            for hit in hits:
                search_results.append(SearchResult(
                    id=hit.entity.get('id'),
                    text=hit.entity.get('text'),
                    distance=hit.distance
                ))

        return SearchResponse(results=search_results)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
