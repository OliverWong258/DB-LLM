from sparkai.llm.llm import ChatSparkLLM, BaseCallbackHandler,Optional,Any
from sparkai.core.messages import ChatMessage

#星火认知大模型Spark Max的URL值，其他版本大模型URL值请前往文档（https://www.xfyun.cn/doc/spark/Web.html）查看
SPARKAI_URL = 'wss://spark-api.xf-yun.com/v1.1/chat'
#星火认知大模型调用秘钥信息，请前往讯飞开放平台控制台（https://console.xfyun.cn/services/bm35）查看
SPARKAI_APP_ID = '20a5f5f7'
SPARKAI_API_SECRET = 'NGMzNTQ2MTEwNzZlNTFiNzFkNDZiNmM1'
SPARKAI_API_KEY = '539d259c8284d57ee874177a2cbd9de1'
#星火认知大模型Spark Max的domain值，其他版本大模型domain值请前往文档（https://www.xfyun.cn/doc/spark/Web.html）查看
SPARKAI_DOMAIN = 'lite'
has_reference=input()
questionPath=input()
referencePath=input()
outputPath=input()
class ChunkPrintHandler(BaseCallbackHandler):
    """Callback Handler that prints to std out."""

    def __init__(self, color: Optional[str] = None) -> None:
        """Initialize callback handler."""
        self.color = color

    def on_llm_new_token(self,  token: str,
        *,
        chunk:  None,
        **kwargs: Any,):
        global islast
        with open(outputPath,'a') as file:
            file.write(str(token))
        # token 为模型生成的token
        # 可以check kwargs内容，kwargs中 llm_output中有usage相关信息， final表示是否是最后一帧
        


if __name__ == '__main__':
    spark = ChatSparkLLM(
        spark_api_url=SPARKAI_URL,
        spark_app_id=SPARKAI_APP_ID,
        spark_api_key=SPARKAI_API_KEY,
        spark_api_secret=SPARKAI_API_SECRET,
        spark_llm_domain=SPARKAI_DOMAIN,
        streaming=True,
    )
    with open(questionPath, "r",encoding='utf-8') as file1:
        question=file1.read()
        if has_reference=='yes':
            with open(referencePath,"r",encoding="utf-8") as file2:
                reference=file2.read()
                messages = [ChatMessage(role="user",content='我接下来会问一个问题，然后提供一些参考文本，请结合我提供的参考文本回答我的问题。'),
                    ChatMessage(role='ai',content="好的，请提供你的问题"),
                    ChatMessage(role='user',content=question),
                    ChatMessage(role='ai',content='好的，我已经收到你的问题，请提供你的参考文本'),
                    ChatMessage(role='user',content=reference)]
                handler = ChunkPrintHandler()
                a = spark.generate([messages], callbacks=[handler])
        else:
            messages=[]
            questions=question.split('@$@')
            for i in range(len(questions)):
                if questions[i]:
                    if i%2==0:
                        messages.append(ChatMessage(role='user',content=questions[i]))
                    else:
                        messages.append(ChatMessage(role='ai',content=questions[i]))
            handler = ChunkPrintHandler()
            a = spark.generate([messages], callbacks=[handler])