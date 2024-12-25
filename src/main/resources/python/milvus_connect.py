from transformers import BertModel, BertTokenizer
from pymilvus import connections, FieldSchema, CollectionSchema, DataType, Collection, utility
import sys
import io

# 强制设置标准输出为 UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 初始化 tokenizer 和模型
tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
model = BertModel.from_pretrained('bert-base-chinese')

# 连接到 Milvus 服务器
connections.connect("default", host="localhost", port="19530")

# 创建或获取集合
collection_name = input() #"policies"
has_collection = utility.has_collection(collection_name)
if not has_collection:
    fields = [
        FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
        FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=768)
    ]
    schema = CollectionSchema(fields, "BERT sentence embeddings")
    collection = Collection(collection_name, schema)
    print(f"集合 '{collection_name}' 创建成功。")
else:
    collection = Collection(collection_name)
    print(f"集合 '{collection_name}' 已存在。")

# 创建索引（如果尚未创建）
if not collection.has_index():
    print(f"集合 '{collection_name}' 尚未创建索引，开始创建索引...")
    index_params = {
        "index_type": "IVF_FLAT",
        "params": {"metric_type": "L2", "nlist": 128}
    }
    collection.create_index(field_name="embedding", index_params=index_params)
    print("索引创建成功。")
else:
    print(f"集合 '{collection_name}' 已存在索引。")

# 加载集合
try:
    collection.load()
    print(f"集合 '{collection_name}' 加载成功。")
except Exception as e:
    print(f"加载集合失败: {e}")