package com.example.demo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class LLMFileIO {
    private String encoding = "UTF-8";
    private String uniqueID;
    public String requestPath;
    public String responsePath;
                
    public LLMFileIO(){
        uniqueID = UUID.randomUUID().toString();
        requestPath = String.format("TXTFiles/request/%s.txt", uniqueID);
        responsePath = String.format("TXTFiles/response/%s.txt", uniqueID);
    }

    public void Write(String content){
        try (FileWriter writer = new FileWriter(requestPath, false)) { // false表示覆盖写入
            writer.write(content);
            System.out.println("内容已成功写入到文件：" + requestPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Read(){
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(responsePath), encoding))) {
            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                //System.out.println("当前行："+line);
                result += line;
            }
            System.out.println("文件已成功读取");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
