package com.example.demo.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.HttpStatusException;

public class WebCrawler {
    public static int maxRetries = 5;

    public static String DailyUpdate(String outputFolder) {
        // 获取当前日期并格式化为yyyy/MM/dd
        LocalDate currentDate = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = currentDate.format(formatter);

        // 构建目标URL
        String yearMonth = formattedDate.substring(0, 7); // 获取yyyy/MM部分
        String url = "https://www.govinfo.gov/bulkdata/FR/" + yearMonth + "/FR-" + formattedDate.replace("/", "-") + ".xml";
        String outputFilePath = outputFolder + "FR-" + formattedDate.replace("/", "-") + ".xml"; // 本地文件路径

        System.out.println("Generated URL: " + url);

        // 检查本地是否已经存在该文件
        File file = new File(outputFilePath);
        if (file.exists()) {
            System.out.println("文件已存在: " + outputFilePath);
            return "File exists";
        } else {
            int attempts = 0;
            while (attempts < maxRetries) {
                try {
                    // 从URL加载文档，设置连接和读取超时时间
                    Document doc = Jsoup.connect(url)
                                        .ignoreContentType(true)
                                        .timeout(10000) // 设置超时时间为10秒，可修改
                                        .get();

                    // 获取XML内容
                    String xmlContent = doc.outerHtml();

                    // 将内容写入本地文件
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                        writer.write(xmlContent);
                    }

                    System.out.println("文件已成功下载到: " + outputFilePath);
                    return outputFilePath;
                } catch (HttpStatusException e) {
                    if (e.getStatusCode() == 404) {
                        System.out.println("资源未找到: " + url);
                        return "Not Found";
                        // 此处表明当日没有更新的联邦公报，可能在该处要反映给客户一些信息，请根据实际情况修改
                    } else {
                        e.printStackTrace();
                        return "Unknown Error";
                    }
                } catch (SocketTimeoutException e) {
                    attempts++;
                    System.out.println("尝试 " + attempts + "连接超时: " + url);
                    if (attempts >= maxRetries) {
                        System.out.println("已达到最大重试次数，连接失败！");
                        return "Connection out of time"; // 达到最大重试次数后返回
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Unkonwn Error";
                }
            }
            return "Unknown Error";
        }
    }
}
