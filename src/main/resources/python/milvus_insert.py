from transformers import BertModel, BertTokenizer
import torch
from pymilvus import connections, FieldSchema, CollectionSchema, DataType, Collection, utility
import sys
import io

# 强制设置标准输入输出为 UTF-8
sys.stdin = io.TextIOWrapper(sys.stdin.buffer, encoding='utf-8')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def initialize_milvus():
    # 初始化 tokenizer 和模型
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertModel.from_pretrained('bert-base-chinese')

    # 连接到 Milvus 服务器
    connections.connect("default", host="localhost", port="19530")
    print("已连接到 Milvus 服务器。")

    # 创建或获取集合
    collection_name = "policies"
    has_collection = utility.has_collection(collection_name)
    if not has_collection:
        fields = [
            FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
            FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=768),
            FieldSchema(name="text", dtype=DataType.VARCHAR, max_length=65535)  # VARCHAR 类型用于存储文本
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
        sys.exit(1)

    return tokenizer, model, collection

def generate_embedding(tokenizer, model, text):
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

def main():
    tokenizer, model, collection = initialize_milvus()

    print("请输入要插入的文本（输入 '-1' 以退出）：")

    while True:
        try:
            # 从标准输入读取一行
            text = sys.stdin.readline()
            if not text:
                break  # EOF
            text = text.strip()
            if text == "-1":
                print("收到终止信号，正在退出...")
                break
            if not text:
                print("输入的文本为空，请重新输入。")
                continue

            # 生成嵌入
            embedding = generate_embedding(tokenizer, model, text)

            # 准备插入的数据
            entities = [
                [embedding],  # 仅插入 embedding 字段，id 自动生成
                [text]        # 插入文本，便于查询时返回
            ]

            # 插入数据
            insert_result = collection.insert(entities)
            print(f"文本 '{text}' 已插入 Milvus，自动生成的 ID: {insert_result.primary_keys}")

        except Exception as e:
            print(f"发生错误: {e}")

    print("Python 脚本已终止。")

if __name__ == "__main__":
    main()
