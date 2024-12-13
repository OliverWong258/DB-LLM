package com.example.demo.util;

import java.util.ArrayList;
import java.io.*;
import java.nio.charset.StandardCharsets;

import com.example.demo.entity.CaseInfo;

public class CaseExtractor {
    private static String filePath = "TXTFiles/caseinfo/caseinfo.txt";

    public static ArrayList<CaseInfo> extractCaseInfo() {
        try {
            // 读取TXT文件，文件名请具体情况具体修改
            File inputFile = new File(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));


            // 读取TXT文件并插入数据到表格
            ArrayList<CaseInfo> caseList = new ArrayList<>();
            String line;
            String type = null, subject = null, summary = null, content = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("type:")) {
                    // 读取type
                    type = line.substring(5).trim();
                    subject = null;
                    summary = null;
                    content = null;
                } else if (line.startsWith("subject:")) { // 读取subject
                    subject = line.substring(8).trim();
                } else if (line.startsWith("summary:")) { // 读取summary
                    summary = line.substring(8).trim();
                } else if (line.startsWith("content:")) { // 读取content
                    content = line.substring(8).trim();
                    // 读取content的多行内容
                    while ((line = reader.readLine()) != null && !line.startsWith("type:") && !line.startsWith("subject:") && !line.startsWith("summary:") && !line.startsWith("content:")) {
                        content += "\n" + line.trim();
                    }
                    // 如果读取到新的type字段或者读到文件末尾，插入前一组数据
                    if (line != null && line.startsWith("type:")) {
                        caseList.add(new CaseInfo(null, type, subject, summary, content));
                        System.out.println(subject);
                        type = line.substring(5).trim();
                        subject = null;
                        summary = null;
                        content = null;
                    }
                }
            }

            // 插入最后一组数据
            if (type != null && subject != null && summary != null && content != null) {
                caseList.add(new CaseInfo(null, type, subject, summary, content));
            }

            reader.close();
            
            return caseList; 
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}