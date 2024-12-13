package com.example.demo;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import com.example.demo.util.WordGenerator;

@SpringBootTest
public class WordGeneratorTest {
       // 测试方法
       @Test
       public void testGenerateWord(){
            String content = "这是一个测试内容，写入到 Word 文档中。";
            String outputPath = "output.docx";  // 输出的 Word 文件路径
            
            try{
                WordGenerator.generateWord(content, outputPath);
                System.out.println("文件保存在"+outputPath);
            }catch(Exception e){
                e.printStackTrace();
            }
       }
}
