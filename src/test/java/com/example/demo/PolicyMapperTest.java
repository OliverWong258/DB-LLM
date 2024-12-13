package com.example.demo;

import com.example.demo.entity.Policy;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.util.LLMFileIO;
import com.example.demo.util.TranslateUsingApi;
import com.example.demo.util.XMLExtractor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.sql.Date;

@SpringBootTest
@Sql(scripts = "/schema.sql")  // 加载 schema.sql 脚本
public class PolicyMapperTest {

    @Autowired
    private PolicyMapper policyMapper;

    @Test
    //@Transactional  // 确保测试后数据不会保存在数据库中
    public void testInsertPolicies() {
        
        String directoryPath = "D:\\Download\\FR-2024\\11\\";
        File directory = new File(directoryPath);
        
        if (directory.exists() && directory.isDirectory()) {
            // 获取该目录下的所有文件和子目录
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    XMLExtractor extractor = XMLExtractor.getInstance(file.getAbsolutePath());
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
                }
            }
        }
    }

    // 测试根据关键词、部门、日期搜索政策
    @Test
    public void testSearchPolicies_WithAllParameters() {
        try{
            String keywords = "eCollection";
            String department = "";
            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd"); 
            java.util.Date date = sdf.parse("2024-01-02");  
            Date publishDate = new Date(date.getTime());
            List<Policy> policies = policyMapper.searchPolicies(keywords, department, publishDate);
            assertNotNull(policies);
            for (Policy policy : policies) {
                System.out.println();
                System.out.println(policy);
                System.out.println();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

