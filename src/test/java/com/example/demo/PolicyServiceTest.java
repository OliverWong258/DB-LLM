package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.PolicyService;
import com.example.demo.entity.Policy;

@SpringBootTest
public class PolicyServiceTest {
    @Autowired
    private PolicyService policyService;

    @Test
    public void testSearchPolicies_AllNull() {
        List<Policy> policies = policyService.searchPolicies(null, null, null);
        assertNotNull(policies);
        // 验证返回的政策数量是否等于数据库中的总记录数
    }

    @Test
    public void testSearchPolicies_OnlyKeywords() {
        String keywords = "";
        List<Policy> policies = policyService.searchPolicies(keywords, null, null);
        assertNotNull(policies);
        for (Policy policy : policies) {
            System.out.println();
            System.out.println(policy);
            System.out.println();
        }
    }
}
