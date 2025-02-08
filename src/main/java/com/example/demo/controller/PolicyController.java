package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import com.example.demo.entity.Policy;
import com.example.demo.service.PolicyService;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/policies")
/**
 * 政策查询控制器，用于查询用户根据关键词、日期、部门查找的政策
 */
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * 根据查询条件获取政策列表
     * @param keywords 关键词
     * @param departmentNo 颁布部门编号，0="bureau of industry and security", 1="department of the treasury"
     * 2="department of state", 3-"department of justice"
     * @param publishDate 发布日期
     * @return 政策列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Policy>> searchPolicies(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) Integer department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishDate) {

        System.out.println("收到政策查询请求");
        System.out.print(String.format("keywords: %s, department: %s, ", 
        keywords, department));
        System.out.print("publishDate");
        System.out.println(publishDate);
        
        // 调用业务逻辑层方法
        return ResponseEntity.ok(policyService.searchPolicies(keywords, department, publishDate));
    }

}

