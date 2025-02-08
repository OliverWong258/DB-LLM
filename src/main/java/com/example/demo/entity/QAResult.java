package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用于返回问答结果和参考依据
 * result为回答结果
 * refMap为参考的资料
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 政策或案件问答的返回结果
 */
public class QAResult {
    private String answer; // 大模型的回答
    private List<SearchResult> refs; // 大模型回答所参考的资料
}
