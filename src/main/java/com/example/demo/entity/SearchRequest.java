package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 封装了向量数据库的查询请求
 */
public class SearchRequest {
    private String collection_name; // 希望查询的集合名称
    private String query_text; // 查找的文本内容
    private int top_k; // 希望返回的结果数量
}
