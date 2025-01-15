package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.demo.util.MilvusClientService;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.AskUsingApi;
import com.example.demo.entity.QAResult;
import com.example.demo.entity.SearchResult;

import java.time.Duration;
import java.time.Instant;

@Service
public class PolicyQAService {

    @Autowired
    private MilvusClientService milvusClientService;

    /**
     * 处理用户的政策问答请求
     * @param question 用户问题
     * @return 回答结果
     */
    public QAResult processPolicyQuestion(String question) {
        System.out.println(String.format("收到问题: %s", question));

        Instant start = Instant.now();
        // 调用大模型提取用户问题中的关键信息，用于在向量数据库中搜索
        LLMFileIO keyInfoFileIO = new LLMFileIO();
        keyInfoFileIO.Write("提取以下内容的关键信息，作为在向量数据库中搜索的关键字。用一行话给出关键字即可，不要分点罗列：\r\n%s".formatted(question));
        AskUsingApi.ask("no", keyInfoFileIO.requestPath, "", keyInfoFileIO.responsePath);
        String keyInfoStr = keyInfoFileIO.Read();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration);

        start = Instant.now();
        // 查询向量数据库
        List<SearchResult> searchResults = milvusClientService.searchTexts("policies", keyInfoStr, 5);
        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println(duration);

        String references = "";
        Integer idx = 0;

        for (SearchResult result : searchResults){
            idx++;
            references += idx.toString() + ". " + result.getText() + "\r\n";
        }

        LLMFileIO questFileIO = new LLMFileIO();
        LLMFileIO refFileIO = new LLMFileIO();

        questFileIO.Write(question);
        refFileIO.Write(references);
        start = Instant.now();
        AskUsingApi.ask("yes", questFileIO.requestPath, refFileIO.requestPath, questFileIO.responsePath);
        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println(duration);

        String answer = questFileIO.Read();
        System.out.println("回答已生成");
        System.out.println(answer);
        return new QAResult(answer, searchResults);
    }
}

