# save_word.py
from docx import Document
import sys

def create_word(content, output_path):
    # 创建一个 Word 文档
    doc = Document()
    doc.add_paragraph(content)  # 将内容添加到文档中
    doc.save(output_path)  # 保存文档到指定路径

if __name__ == "__main__":
    # 接收命令行传入的参数
    if len(sys.argv) != 3:
        print("Usage: python save_word.py <content> <output_path>")
    else:
        content = sys.argv[1]  # 第一个参数：内容
        output_path = sys.argv[2]  # 第二个参数：输出路径
        create_word(content, output_path)
