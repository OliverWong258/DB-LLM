package com.example.demo.util;

import java.io.*;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

public class MilvusInserter {

    private Process pythonProcess;
    private BufferedWriter pythonWriter;
    private BufferedReader pythonReader;
    private BufferedReader pythonErrorReader;

    /**
     * 启动 Python 脚本作为子进程
     *
     * @param pythonScriptPath Python 脚本的绝对路径
     * @throws IOException 如果启动进程失败
     */
    public void startPythonScript(String pythonScriptPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        pythonProcess = processBuilder.start();

        // 获取 Python 脚本的标准输入和输出
        pythonWriter = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream(), StandardCharsets.UTF_8));
        pythonReader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream(), StandardCharsets.UTF_8));
        pythonErrorReader = new BufferedReader(new InputStreamReader(pythonProcess.getErrorStream(), StandardCharsets.UTF_8));

        // 启动一个线程来读取 Python 脚本的输出（可选）
        new Thread(() -> {
            String line;
            try {
                while ((line = pythonReader.readLine()) != null) {
                    System.out.println("[Python]: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 启动一个线程来读取 Python 脚本的错误输出（可选）
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
    public void insertText(String text) throws IOException {
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
    public void stopPythonScript() throws IOException {
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

    public void insertTexts(ArrayList<String> textsToInsert) {
        String pythonScriptPath = "src\\main\\resources\\python\\milvus_insert.py";
        try{
            // 启动 Python 脚本
            this.startPythonScript(pythonScriptPath);

            // 逐个插入文本
            for (String text : textsToInsert) {
                this.insertText(text);
                // 可以根据需要添加延时，例如 Thread.sleep(1000);
            }

            // 发送终止信号
            this.stopPythonScript();

            // 等待 Python 脚本结束
            this.waitForProcess();

            System.out.println("所有文本已插入，并成功终止 Python 脚本。");
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}

