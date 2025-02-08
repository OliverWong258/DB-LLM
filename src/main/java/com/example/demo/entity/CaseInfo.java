package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 案件信息实体类，封装了一个案件
 */
public class CaseInfo {
    private Integer id;               // ID
    private String type;              // 案件类型
    private String chineseSubject;    // 案件的中文主题
    private String chineseSummary;    // 案件的中文摘要 
    private String content;           // 案件的完整内容
}
