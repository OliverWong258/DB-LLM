package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 封装了插入向量数据库的内容
 */
public class TextToInsert {
    private int id; // 插入的信息的编号（与mysql数据库一致）
    private String text; // 插入的信息的文本内容
}