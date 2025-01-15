package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.mapper.PolicyMapper;
import com.example.demo.entity.*;
import com.example.demo.util.MilvusClientService;

@SpringBootTest
public class MilvusTest {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private MilvusClientService milvusClientService;

    @Test
    public void testMilvusSearch(){

        String question = "药品制造和生产";

        List<SearchResult> searchResults = milvusClientService.searchTexts("policies", question, 5);
        System.out.println("查询结果:");
        System.out.println(searchResults);
        System.out.println();
    }

    @Test
    public void testMilvusInsertAllPolicies(){
        //String dateString = "2025-01-02";

        List<Policy> policies = policyMapper.searchPolicies(null, null, null);

        List<TextToInsert> textList = new ArrayList<>();
        for (Policy policy:policies){
            textList.add(new TextToInsert(policy.getId(), policy.getChineseSummary()));
        }

        milvusClientService.insertTexts("policies", textList);
    }
}
