package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 用于返回问答结果和参考依据
 * result为回答结果
 * refMap为参考的资料
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAResult {
    private String result;
    private Map<Integer, String> refMap;
}
