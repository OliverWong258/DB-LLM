package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseInfo {
    private Integer id;               // ID
    private String type;              // 类型
    private String chineseSubject;    // 中文主题
    private String chineseSummary;    // 中文摘要 
    private String content;           // 内容
}
