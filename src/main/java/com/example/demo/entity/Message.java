package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {//暂定，需要根据实际功能修改
    /**
     * 字符串类信息
     */
    private String content;
}

