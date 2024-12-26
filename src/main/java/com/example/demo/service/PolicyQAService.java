package com.example.demo.service;

import org.springframework.stereotype.Service;

//import com.example.demo.entity.Message;
import com.example.demo.util.MilvusAPI;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.AskUsingApi;

@Service
public class PolicyQAService {

    /**
     * 处理用户的政策问答请求
     * @param question 用户问题
     * @return 回答结果
     */
    public String processPolicyQuestion(String question) {
        System.out.println(String.format("收到问题: %s", question));
        
        // 解析问题，检索信息，调用大模型生成回答
        MilvusAPI milvusAPI = new MilvusAPI();

        String reference = milvusAPI.searchTexts(question);

        LLMFileIO questFileIO = new LLMFileIO();
        
        String requestStr = String.format("我接下来会问一个问题，然后提供一些参考文本，请结合我提供的参考文本回答我的问题。@$@\r\n" + //
                        " 好的，请提供你的问题。@$@@$@@$@\r\n" + //
                        " %s@$@\r\n" + //
                        " 好的，我已经收到你的问题，请提供你的参考文本@$@\r\n" + //
                        " %s\r\n" + //
                        " @$@", question, reference);

        questFileIO.Write(requestStr);
        AskUsingApi.ask("no", questFileIO.requestPath, "", questFileIO.responsePath);
        
        String result = questFileIO.Read();
        System.out.println("结果: ");
        System.out.println(result);
        return result;
    }
}

