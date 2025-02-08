package com.example.demo.util;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 按一定的规则生成两个TXT文件，一个用于存储发送给大模型的文本，
 * 另一个用于存储大模型的回答
 */
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
        } catch (IOException e) { // 出现异常是因为触犯了大模型的内部规定
            //e.printStackTrace();
            return " 非常抱歉，根据相关法律法规，我们无法提供关于以下内容的 答案，包括但不限于：\r\n" + //
                                "        (1) 涉及国家安全的信息；\r\n" + //
                                "        (2) 涉及政治与宗教类的信息；\r\n" + //
                                "        (3) 涉及暴力与恐怖主义的信息；\r\n" + //
                                "        (4) 涉及黄赌毒类的信息；\r\n" + //
                                "        (5) 涉及不文明的信息。\r\n" + //
                                "我们会继续遵循相关法规法律的要求，共创一个健康和谐网络环境，谢谢您的理解。";
        }
    }
}
