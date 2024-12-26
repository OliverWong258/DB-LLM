package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.demo.entity.Message;
import com.example.demo.service.PolicyQAService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/qna")
public class QnAController {

    @Autowired
    private PolicyQAService qnAService;

    /**
     * 提交用户的问题，获取政策相关的回答
     * @param question 用户问题
     * @return 回答结果
     */
    @PostMapping("/policy")
    public ResponseEntity<Message> askPolicyQuestion(@RequestBody Message question) {
        // 调用业务逻辑层方法
        Message responseMsg = new Message();
        responseMsg.setContent(qnAService.processPolicyQuestion(question.getContent()));
        System.out.println(String.format("content内容: %s", responseMsg.getContent()));
        return ResponseEntity.ok(responseMsg);
    }
}

