package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Policy;
import com.example.demo.mapper.PolicyMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;

@Service
/**
 * 处理政策查询请求
 */
public class PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    private Map<Integer, String> departmentMap=new HashMap<>();

    private Map<String, String> departmentEN2CH=new HashMap<>();

    // 提供部门和编号之间的映射。因为部门是固定的，所以可以直接根据编号查找
    PolicyService(){
        departmentMap.put(0, "bureau of industry and security");
        departmentMap.put(1, "department of the treasury");
        departmentMap.put(2, "department of state");
        departmentMap.put(3, "department of justice");
        departmentEN2CH.put("bureau of industry and security","工业和安全局");
        departmentEN2CH.put("department of the treasury", "财政部");
        departmentEN2CH.put("department of state","国务院");
        departmentEN2CH.put("department of justice", "司法部");
    }

    /**
     * 根据查询条件搜索政策
     * @param keywords 关键词
     * @param department 颁布部门
     * @param publishDate 发布日期
     * @return 政策列表
     */
    @SuppressWarnings("null")
    public List<Policy> searchPolicies(String keywords, Integer departmentNo, LocalDate publishDate) {
        Date date = publishDate != null ? Date.valueOf(publishDate) : null;
        //return policyMapper.searchPolicies(keywords, department, date);
        // 调用方法查找政策

        String department = departmentMap.get(departmentNo);
        List<Policy> policies = policyMapper.searchPolicies(keywords, department, date);

        // 如果查找到的政策不为空，则输出 "已找到"
        if (policies != null && !policies.isEmpty()) {
            System.out.println("已找到");
        }

        for (Policy policy:policies){
            //policy.setAgency(departmentEN2CH.get(policy.getAgency()));
            policy.setAgency(departmentEN2CH.getOrDefault(policy.getAgency(), departmentEN2CH.get(policy.getSubagency())));

        }

        return policies;
    }
    
}

