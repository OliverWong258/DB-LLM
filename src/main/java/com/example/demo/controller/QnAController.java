package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.demo.entity.Message;
import com.example.demo.entity.QAResult;
import com.example.demo.service.PolicyQAService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/qna")
/**
 * 政策问答控制器，用于回答用户关于政策的问题
 */
public class QnAController {

    @Autowired
    private PolicyQAService qnAService;

    /**
     * 提交用户的问题，获取政策相关的回答
     * @param question 用户问题
     * @return 回答结果
     */
    @PostMapping("/policy")
    public ResponseEntity<QAResult> askPolicyQuestion(@RequestBody Message question) {
        // 调用业务逻辑层方法
        QAResult responseREsult = qnAService.processPolicyQuestion(question.getContent());
        return ResponseEntity.ok(responseREsult);
    }
}

