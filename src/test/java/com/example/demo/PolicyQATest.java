package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import com.example.demo.service.PolicyQAService;

@SpringBootTest
public class PolicyQATest {

    @Autowired
    PolicyQAService policyQAService = new PolicyQAService();
    
    @Test
    public void testAsking(){
        String question = "今天工业安全局发布了哪些文件？";

        System.out.println(policyQAService.processPolicyQuestion(question));
    }
}
