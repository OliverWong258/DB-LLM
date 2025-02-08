package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.CaseInfoMapper;
import com.example.demo.mapper.PolicyMapper;

@Service
/**
 * 用于返回政策或案件的全部内容
 */
public class FullContent {
    
    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private CaseInfoMapper caseInfoMapper;

    // tableNo: 1代表政策，2代表案件
    public String getFullContent(int tableNo, int id){
        switch (tableNo) {
            case 1:
                return policyMapper.searchPolicyById(id).getContent(); // 政策的全部内容
                //break;
            
            case 2:
                return caseInfoMapper.searchCases(id).get(0).getContent(); // 案件的全部内容
            default:
                return "tabel does not exist";
        }
    }
}
