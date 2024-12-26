package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap;
import java.util.Map;

import com.example.demo.util.MilvusAPI;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.entity.Policy;

@SpringBootTest
public class MilvusTest {

    @Autowired
    PolicyMapper policyMapper;

    @Test
    public void testMilvusSearch(){
        MilvusAPI milvusInserter = new MilvusAPI();

        String question = "";

        String result = milvusInserter.searchTexts(question);
        System.out.println("查询结果:");
        System.out.println(result);
        System.out.println();
    }

    @Test
    public void testMilvusInsertAll(){
        List<Policy> policies = policyMapper.searchPolicies(null, null, null);

        MilvusAPI milvusAPI = new MilvusAPI();

        ArrayList<Map.Entry<Integer, String>> policiesToInsert = new ArrayList<>();
        for (Policy policy:policies){
            if (policy.getChineseSummary().length() > 2){
                policiesToInsert.add(new AbstractMap.SimpleEntry<>(policy.getId(), policy.getChineseSummary()));
            }
        }

        milvusAPI.insertTexts(policiesToInsert);
    }
}
