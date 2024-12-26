package com.example.demo.util;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(requestPath), encoding))) {
            writer.write(content);
            System.out.println("内容已成功写入到文件：" + requestPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Read(){
        try {
            String result = Files.readString(Paths.get(responsePath), StandardCharsets.UTF_8);
            System.out.println("文件已成功读取");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
