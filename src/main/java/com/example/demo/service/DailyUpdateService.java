package com.example.demo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.entity.Policy;
import com.example.demo.entity.TextToInsert;
import com.example.demo.util.WebCrawler;
import com.example.demo.util.XMLExtractor;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.util.TranslateUsingApi;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.DeleteFilesInDirectory;
import com.example.demo.util.MilvusClientService;

@Service
/**
 * 每日更新mysql数据库和向量数据库的政策数据
 * 由于联邦公报每天更新的时间是不固定的，因而当日爬取可能会错过更新时间。虽然可以一天爬取多次，但这样产生了不必要的开销。
 * 因而，我们实际更新的是前一天的政策数据（如1.17号爬取1.16号的），这样也能不断地更新，且不会错过时间。
 */
public class DailyUpdateService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private MilvusClientService milvusClientService;

    @Value("${app.xml.storage.path}")
    private String xmlStoragePath;

    private String requestPath = "TXTFiles/request/";
    private String responsePath = "TXTFiles/response/";

    // 秒 分 时 日 月 周
    @Scheduled(cron = "0 5 19 * * ?")
    public void updateData() {
        // 调用爬虫脚本
        System.out.println("开始爬取最新的xml文件");
        String result = WebCrawler.DailyUpdate(xmlStoragePath); // 若爬取成功，返回文件路径名result
        
        switch (result) {
            case "File exists":
                break;
            case "Not Found":
                break;
            case "Connection out of time":
                break;
            case "Unkonwn Error":
                break;
            default:
                XMLExtractor extractor = XMLExtractor.getInstance(result);
                List<Policy> policies = extractor.extractPolicy();
                List<TextToInsert> textList = new ArrayList<>();

                // 插入每个 Policy 对象
                for (Policy policy : policies) {
                    try{
                        // 若关键词或摘要缺失则略过
                        if(policy.getSubjectJson() == null || policy.getSubjectJson().toString() == ""
                        || policy.getSummary() == "") continue;

                        // 翻译关键词
                        LLMFileIO subjectFileIO = new LLMFileIO();
                        subjectFileIO.Write(policy.getSubjectJson().toString());
                        TranslateUsingApi.translate(subjectFileIO.requestPath, subjectFileIO.responsePath);
                        policy.setChineseSubject(subjectFileIO.Read());

                        // 翻译摘要
                        LLMFileIO summaryFileIO = new LLMFileIO();
                        summaryFileIO.Write(policy.getSummary());
                        TranslateUsingApi.translate(summaryFileIO.requestPath, summaryFileIO.responsePath);
                        policy.setChineseSummary(summaryFileIO.Read());

                        policyMapper.insertDocument(policy);

                        System.out.println("MySQL数据库更新成功");

                        // 准备插入到Milvus
                        textList.add(new TextToInsert(policy.getId(), policy.getChineseSummary()));
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println(policy.getDate());
                    }
                }
                // 插入到Milvus
                milvusClientService.insertTexts("policies", textList);
                break;
        }
    }

    // 由于和大模型的交互会产生一些临时的TXT文档，因而需要定期清理
    // 秒 分 时 日 月 周
    @Scheduled(cron = "0 10 13 * * ?")
    public void deleteTXT(){
        if (DeleteFilesInDirectory.deleteAllFilesInDirectory(requestPath)){
            System.out.println("请求文本删除成功");
        }
        else{
            System.out.println("请求文本删除失败");
        }
        if (DeleteFilesInDirectory.deleteAllFilesInDirectory(responsePath)){
            System.out.println("响应文本删除成功");
        }
        else{
            System.out.println("响应文本删除失败");
        }
    }
}