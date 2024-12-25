package com.example.demo.util;

import java.io.*;

public class MilvusAPI {
    public MilvusAPI(String CollectionName){
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "src\\main\\resources\\python\\milvus_connect.py");

            // 启动进程
            Process process = pb.start();

            // -------------------------------
            // 1. 向 Python 脚本的标准输入写入数据
            // -------------------------------
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bw.write(CollectionName);  // 这里写入你想要传递的字符串
            bw.newLine();               // 写完后记得换行，让 input() 能读取到
            bw.flush();
            bw.close(); // 如果后续不再写入，可以关闭输出流

            // -------------------------------
            // 2. 读取 Python 脚本的输出
            // -------------------------------
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Python output: " + line);
            }
            br.close();

            // -------------------------------
            // 3. 等待脚本执行结束并获取退出码（可选）
            // -------------------------------
            int exitCode = process.waitFor();
            System.out.println("Script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Insert(String content){
        
    }
}
