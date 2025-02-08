package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 向量数据库的查询结果
 */
public class SearchResult {
    private int id; // 参考信息在向量数据库中的编号
    private String text; // 具体的参考信息
    private float distance; // 参考信息与用户问题的相近程度
}
