package com.example.demo.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 封装发送给python后端的插入milvus向量数据库的请求
 */
public class InsertRequest {
    private String collection_name; // 需要插入的milvus集合名称
    private List<TextToInsert> texts; // 需要插入的文本内容
}