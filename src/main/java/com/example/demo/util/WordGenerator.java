package com.example.demo.util;

import java.io.*;

public class WordGenerator {

    // 静态方法，调用 Python 脚本生成 Word 文档
    public static void generateWord(String content, String outputPath) {
        try {
            // 构建命令行调用
            String pythonScriptPath = "python src/main/resources/python/save_word.py"; // 你需要根据实际情况修改脚本路径
            String command = pythonScriptPath + " \"" + content + "\" \"" + outputPath + "\"";
            
            // 调用 Python 脚本
            Process process = Runtime.getRuntime().exec(command);

            // 获取 Python 脚本的输出（标准输出和错误输出）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 打印标准输出
            }
            
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);  // 打印错误输出
            }

            // 等待 Python 脚本执行完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Word document created successfully.");
            } else {
                System.err.println("Error occurred while creating the Word document.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

