package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;

//import com.example.demo.entity.Message;
import com.example.demo.util.MilvusAPI;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.AskUsingApi;
import com.example.demo.entity.QAResult;;

@Service
public class PolicyQAService {

    /**
     * 处理用户的政策问答请求
     * @param question 用户问题
     * @return 回答结果
     */
    public QAResult processPolicyQuestion(String question) {
        System.out.println(String.format("收到问题: %s", question));
        
        // 解析问题，检索信息，调用大模型生成回答
        MilvusAPI milvusAPI = new MilvusAPI("policies");

        Map<Integer, String> refMap = milvusAPI.searchTexts(question);

        System.out.println("向量数据库查询完成");

        String reference = "";
        Integer idx = 0;

        for (int key : refMap.keySet()){
            idx++;
            reference += idx.toString() + refMap.get(key) + "\r\n";
        }

        LLMFileIO questFileIO = new LLMFileIO();
        LLMFileIO refFileIO = new LLMFileIO();

        questFileIO.Write(question);
        refFileIO.Write(reference);
        AskUsingApi.ask("yes", questFileIO.requestPath, refFileIO.requestPath, questFileIO.responsePath);
        
        String result = questFileIO.Read();
        System.out.println("回答已生成");
        return new QAResult(result, refMap);
    }
}

