from transformers import BertModel, BertTokenizer
import torch
from pymilvus import connections, Collection, utility
import sys
import io

# 设置标准输出和输入为 UTF-8 编码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stdin = io.TextIOWrapper(sys.stdin.buffer, encoding='utf-8')

def initialize_milvus():
    # 初始化 tokenizer 和模型
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertModel.from_pretrained('bert-base-chinese')

    # 连接到 Milvus 服务器
    connections.connect("default", host="localhost", port="19530")
    #print("已连接到 Milvus 服务器。")

    # 加载集合
    collection_name = "policies"
    if not utility.has_collection(collection_name):
        print(f"集合 '{collection_name}' 不存在，请先创建并插入数据。", file=sys.stderr)
        sys.exit(1)
    else:
        collection = Collection(collection_name)
        #print(f"集合 '{collection_name}' 已存在。")

    # 加载集合
    try:
        collection.load()
        #print(f"集合 '{collection_name}' 加载成功。")
    except Exception as e:
        print(f"加载集合失败: {e}", file=sys.stderr)
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

    query_text = input().strip() # input("请输入查询文本:\n").strip()
    if not query_text:
        print("输入的查询文本为空，程序退出。", file=sys.stderr)
        sys.exit(0)

    # 生成嵌入
    query_embedding = generate_embedding(tokenizer, model, query_text)

    # 定义搜索参数
    search_params = {
        "metric_type": "L2",  # 使用的距离度量
        "params": {"nprobe": 10}  # 搜索参数
    }

    # 执行搜索，返回最相似的5个向量
    results = collection.search(
        data=[query_embedding],          # 查询向量
        anns_field="embedding",          # 搜索的字段
        param=search_params,
        limit=3,                         # 返回的结果数量
        expr=None,                       # 可选的过滤条件
        output_fields=["text"]           # 要返回的字段
    )

    # 输出搜索结果
    for hits in results:
        for hit in hits:
            print(hit.entity.get('text'))

if __name__ == "__main__":
    main()
