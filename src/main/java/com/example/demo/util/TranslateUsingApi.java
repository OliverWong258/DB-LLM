package com.example.demo.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TranslateUsingApi{
    /*
    * @description:调用该静态方法，将需要翻译的txt文件翻译后以txt文件的形式产出。
    * @param: textPath-需要翻译的文件的路径,translationPath-翻译结果保存路径。
    * @tip:如果程序不报错，但运行之后无事发生，大概率是参数给的路径有问题。
     */
    public static void translate(String textPath,String translationPath) {
        try {
            String pythonScriptPath = "src\\main\\resources\\python\\TranslationScript.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            Process process = processBuilder.start();
            PrintWriter outputWriter=new PrintWriter(process.getOutputStream(),true);
            outputWriter.print(textPath);
            outputWriter.print("\n");
            outputWriter.flush();
            outputWriter.print(translationPath);
            outputWriter.print("\n");
            outputWriter.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Handle the error
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}