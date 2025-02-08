package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.mapper.PolicyMapper;
import com.example.demo.entity.*;
import com.example.demo.util.MilvusClientService;

@SpringBootTest
/**
 * 测试与向量数据库相关的操作
 */
public class MilvusTest {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private MilvusClientService milvusClientService;

    @Test
    /**
     * 根据关键信息从向量数据库中搜索相关的内容
     */
    public void testMilvusSearch(){

        String question = "";

        List<SearchResult> searchResults = milvusClientService.searchTexts("policies", question, 5);
        System.out.println("查询结果:");
        System.out.println(searchResults);
        System.out.println();
    }

    @Test
    /**
     * 测试向milvus数据库中插入信息
     */
    public void testMilvusInsertAllPolicies(){
        String dateString = "";

        List<Policy> policies = policyMapper.searchPolicies("", "", Date.valueOf(dateString));

        List<TextToInsert> textList = new ArrayList<>();
        for (Policy policy:policies){
            textList.add(new TextToInsert(policy.getId(), policy.getChineseSummary()));
        }

        milvusClientService.insertTexts("policies", textList);
    }
}
