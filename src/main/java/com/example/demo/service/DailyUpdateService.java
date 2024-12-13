package com.example.demo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.entity.Policy;
import com.example.demo.util.WebCrawler;
import com.example.demo.util.XMLExtractor;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.util.TranslateUsingApi;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.DeleteFilesInDirectory;

@Service
public class DailyUpdateService {

    @Autowired
    private PolicyMapper policyMapper;

    @Value("${app.xml.storage.path}")
    private String xmlStoragePath;

    private String requestPath = "TXTFiles/request/";
    private String responsePath = "TXTFiles/response/";

    // 秒 分 时 日 月 周
    @Scheduled(cron = "0 30 12 * * ?")
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

                // 插入每个 Policy 对象
                for (Policy policy : policies) {
                    try{
                        // 翻译关键词
                        if (policy.getSubjectJson().toString() != null && !policy.getSubjectJson().toString().trim().isEmpty()){
                            LLMFileIO llmFileIO = new LLMFileIO();
                            llmFileIO.Write(policy.getSubjectJson().toString());
                            TranslateUsingApi.translate(llmFileIO.requestPath, llmFileIO.responsePath);
                            policy.setChineseSubject(llmFileIO.Read());
                        }
                        // 翻译摘要
                        if (policy.getSummary() != ""){
                            LLMFileIO llmFileIO = new LLMFileIO();
                            llmFileIO.Write(policy.getSummary());
                            TranslateUsingApi.translate(llmFileIO.requestPath, llmFileIO.responsePath);
                            policy.setChineseSummary(llmFileIO.Read());
                        }

                        policyMapper.insertDocument(
                                policy.getType(),
                                policy.getDate(),
                                policy.getDayOfTheWeek(),
                                policy.getAgency(),
                                policy.getSubagency(),
                                policy.getSubjectJson().toString(),
                                policy.getChineseSubject(),
                                policy.getCfr(),
                                policy.getDepdoc(),
                                policy.getFrdoc(),
                                policy.getBilcod(),
                                policy.getSummary(),
                                policy.getChineseSummary(),
                                policy.getContent()
                        );
                        System.out.println("数据库更新成功");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println(policy.getDate());
                    }
                }
                break;
        }
    }

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