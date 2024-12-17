package com.example.demo.service;

import org.springframework.stereotype.Service;

//import com.example.demo.entity.Message;
import com.example.demo.util.WordGenerator;

@Service
public class ReportService {

    /**
     * 根据回答结果生成Word简报
     * @param reportContent 报告内容
     * @return 下载链接
     */
    public String generateReport(String reportContent) {
        // 实现Word文档生成逻辑
        return WordGenerator.generateWord(reportContent);
    }
}

