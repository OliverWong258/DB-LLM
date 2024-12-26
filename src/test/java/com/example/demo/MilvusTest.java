package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

//import com.example.demo.util.MilvusAPI;
import com.example.demo.util.MilvusAPI;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.entity.Policy;

@SpringBootTest
public class MilvusTest {

    @Autowired
    PolicyMapper policyMapper;

    @Test
    public void testMilvusInsert(){
        //MilvusAPI milvusInsert = new MilvusAPI("policies");
        MilvusAPI milvusInserter = new MilvusAPI();

        ArrayList<String> textsToInsert = new ArrayList<>();
        textsToInsert.add("这是第一条测试文本。");
        textsToInsert.add("这是第二条测试文本。");
        textsToInsert.add("这是第三条测试文本。");

        milvusInserter.insertTexts(textsToInsert);
    }

    @Test
    public void testMilvusSearch(){
        MilvusAPI milvusInserter = new MilvusAPI();

        String question = "今天工业安全局发布了哪些文件？";

        String result = milvusInserter.searchTexts(question);
        System.out.println("查询结果:");
        System.out.println(result);
        System.out.println();
    }

    @Test
    public void testMilvusInsertAll(){
        List<Policy> policies = policyMapper.searchPolicies(null, null, null);

        MilvusAPI milvusAPI = new MilvusAPI();

        ArrayList<String> policiesToInsert = new ArrayList<>();
        for (Policy policy:policies){
            policiesToInsert.add(policy.getChineseSummary());
        }

        milvusAPI.insertTexts(policiesToInsert);
    }
}
