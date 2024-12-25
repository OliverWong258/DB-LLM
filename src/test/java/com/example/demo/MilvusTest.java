package com.example.demo;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

//import com.example.demo.util.MilvusAPI;
import com.example.demo.util.MilvusInserter;

@SpringBootTest
public class MilvusTest {
    @Test
    public void MilvusInsertTest(){
        //MilvusAPI milvusInsert = new MilvusAPI("policies");
        MilvusInserter milvusInserter = new MilvusInserter();

        ArrayList<String> textsToInsert = new ArrayList<>();
        textsToInsert.add("这是第一条测试文本。");
        textsToInsert.add("这是第二条测试文本。");
        textsToInsert.add("这是第三条测试文本。");

        milvusInserter.insertTexts(textsToInsert);
    }
}
