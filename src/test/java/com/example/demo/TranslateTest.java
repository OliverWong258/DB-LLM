package com.example.demo;

import com.example.demo.util.TranslateUsingApi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TranslateTest {
    String textPath = "";
    String translatePath = "";

    @Test
    public void HelloWorldTest(){
        System.out.println("开始翻译");
        TranslateUsingApi.translate(textPath, translatePath);
        System.out.println("翻译结束");
    }
}
