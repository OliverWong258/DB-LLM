package com.example.demo.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class MilvusAPI {

    private String collectionName; // 选择案件或者政策集合
    private Process pythonProcess;
    private BufferedWriter pythonWriter;
    private BufferedReader pythonReader;
    private BufferedReader pythonErrorReader;
    private Map<Integer, String> outputMap = new HashMap<>();

    public MilvusAPI(String collectionName){
        this.collectionName = collectionName;
    }

    /**
     * 启动 Python 脚本作为子进程
     *
     * @param pythonScriptPath Python 脚本的绝对路径
     * @throws IOException 如果启动进程失败
     */
    public void startPythonScript(String pythonScriptPath, Boolean recordOutput) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        pythonProcess = processBuilder.start();

        // 获取 Python 脚本的标准输入和输出
        pythonWriter = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream(), StandardCharsets.UTF_8));
        pythonReader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream(), StandardCharsets.UTF_8));
        pythonErrorReader = new BufferedReader(new InputStreamReader(pythonProcess.getErrorStream(), StandardCharsets.UTF_8));

        // 启动一个线程来读取 Python 脚本的输出
        if (recordOutput){
            new Thread(() -> {
                String line;
                ArrayList<String> parts = new ArrayList<>();
                try {
                    while ((line = pythonReader.readLine()) != null) {
                        //System.out.println(line);
                        parts.add(line);

                        if (parts.size() == 2) {
                            // 解析 id（整型）和文本（字符串）
                            try {
                                int id = Integer.parseInt(parts.get(0).trim());  // 转换为整型
                                String text = parts.get(1).trim();  // 处理文本部分

                                // 存储到 Map 中
                                outputMap.put(id, text);
                                parts.clear();
                            } catch (NumberFormatException e) {
                                // 如果 id 不是一个合法的整型，跳过这一行
                                System.out.println("无效的 id: " + parts.get(0));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 启动一个线程来读取 Python 脚本的错误输出
        new Thread(() -> {
            String line;
            try {
                while ((line = pythonErrorReader.readLine()) != null) {
                    System.err.println("[Python Error]: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 向 Python 脚本发送文本进行插入
     *
     * @param text 要插入的文本
     * @throws IOException 如果写入失败
     */
    public void writeCommand(String text) throws IOException {
        if (pythonWriter != null) {
            pythonWriter.write(text);
            pythonWriter.newLine();
            pythonWriter.flush();
        } else {
            throw new IOException("Python 脚本未启动或已关闭。");
        }
    }

    /**
     * 向 Python 脚本发送终止信号
     *
     * @throws IOException 如果写入失败
     */
    public void stopPythonInsertScript() throws IOException {
        if (pythonWriter != null) {
            pythonWriter.write("-1");
            pythonWriter.newLine();
            pythonWriter.flush();
            pythonWriter.close();
        }
    }

    /**
     * 等待 Python 脚本进程结束
     *
     * @throws InterruptedException 如果等待过程中被中断
     */
    public void waitForProcess() throws InterruptedException {
        if (pythonProcess != null) {
            pythonProcess.waitFor();
        }
    }

    // 批量插入
    public void insertTexts(ArrayList<Map.Entry<Integer, String>> textsToInsert) {
        String pythonScriptPath = "src\\main\\resources\\python\\milvus_insert.py";
        try{
            // 启动 Python 脚本
            this.startPythonScript(pythonScriptPath, false);

            // 指定集合名称
            this.writeCommand(this.collectionName);

            // 逐个插入文本
            for (Map.Entry<Integer, String> text : textsToInsert) {
                this.writeCommand(text.getKey().toString());
                this.writeCommand(text.getValue());
                System.out.println("文本插入成功");
                //Thread.sleep(10);
            }

            // 发送终止信号
            this.stopPythonInsertScript();

            // 等待 Python 脚本结束
            this.waitForProcess();

            System.out.println("所有文本已插入，并成功终止 Python 脚本。");
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    //搜索
    public Map<Integer, String> searchTexts(String question){
        String pythonScriptPath = "src\\main\\resources\\python\\milvus_search.py";
        try{
            this.startPythonScript(pythonScriptPath, true);

            this.writeCommand(question);

            this.waitForProcess();

            return this.outputMap;
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

