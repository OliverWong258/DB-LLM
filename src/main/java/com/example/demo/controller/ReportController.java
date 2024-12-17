package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.example.demo.entity.Message;
import com.example.demo.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 生成Word简报并返回下载链接
     * @param reportRequest 报告请求内容
     * @return 下载链接
     */
    @PostMapping("/generate")
    public ResponseEntity<Resource> generateReport( @RequestBody Message reportMessage) throws IOException {
        // 调用业务逻辑层方法
        String filePath = reportService.generateReport(reportMessage.getContent());
        File file = new File(filePath);

        Resource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Report.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

