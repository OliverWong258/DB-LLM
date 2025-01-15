package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.QAResult;
import com.example.demo.entity.SearchResult;
import com.example.demo.util.AskUsingApi;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.MilvusClientService;

//import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CaseAnalysisService {

    @Autowired
    private MilvusClientService milvusClientService;

    /**
     * 处理用户的案件分析问答请求
     * @param question 用户问题
     * @return 回答结果
     */
    public QAResult processCaseQuestion(String question) {
        System.out.println(String.format("收到问题: %s", question));

        // 调用大模型提取用户问题中的关键信息，用于在向量数据库中搜索
        LLMFileIO keyInfoFileIO = new LLMFileIO();
        keyInfoFileIO.Write("提取以下内容的关键信息，作为在向量数据库中搜索的关键字。用一行话给出关键字即可，不要分点罗列：\r\n%s".formatted(question));
        AskUsingApi.ask("no", keyInfoFileIO.requestPath, "", keyInfoFileIO.responsePath);
        String keyInfoStr = keyInfoFileIO.Read();

        // 查询向量数据库
        List<SearchResult> searchResults = milvusClientService.searchTexts("cases", keyInfoStr, 3);

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
        AskUsingApi.ask("yes", questFileIO.requestPath, refFileIO.requestPath, questFileIO.responsePath);

        String answer = questFileIO.Read();
        System.out.println("回答已生成");
        System.out.println(answer);
        return new QAResult(answer, searchResults);
    }
}
