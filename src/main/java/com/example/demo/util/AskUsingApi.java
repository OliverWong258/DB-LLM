package com.example.demo.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class AskUsingApi{
    /*
    * @description:调用该静态方法，向大模型提出问题，有两种模式，具体见代码末尾的注释
    * @param: has_reference-模式选项（“yes”或“no”）,
    *           questionPath-存放着问题的txt文件路径，
    *           referencePath-存放着参考文档的txt文件路径，
    *           outputPath-大模型回答的保存路径。
    * @tip:如果程序不报错，但运行之后无事发生，大概率是参数给的路径有问题。
     */
    public static void ask(String has_reference,String questionPath,String referencePath,String outputPath) {
        try {
            String pythonScriptPath = "src\\main\\resources\\python\\AskScript.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            Process process = processBuilder.start();
            PrintWriter outputWriter=new PrintWriter(process.getOutputStream(),true);
            outputWriter.print(has_reference);
            outputWriter.print("\n");
            outputWriter.flush();
            outputWriter.print(questionPath);
            outputWriter.print("\n");
            outputWriter.flush();
            outputWriter.print(referencePath);
            outputWriter.print("\n");
            outputWriter.flush();
            outputWriter.print(outputPath);
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
/*
 * 关于该方法的两种模式，介绍如下：
 * 
 * has_reference=="yes":
 *      代表采用预先设置的模板，输入问题和参考文档后会自动生成prompt，不用再去手动配置。
 * 
 * 下面是这种模式下question的一个示例：
 * “请先介绍一下北京大学，然后再介绍一下北京大学JAVA课程第五组”
 * 
 * 下面是这种模式下reference的一个示例：
 * “北京大学JAVA课程一共有六组，其中第五组由5位同学组成，包括2名来自北京大学信息管理系的同学、两名来自北京大学工学院的同学和1名来自北京大学信息科学技术学院的同学。”
 * 
 * 
 * 
 * 
 * 
 * 
 * has_reference=="no":
 *      代表手动配置问题，此时referencePath随便填就行（因为这个参数用不到）。
 *      在question中，用“@$@"分隔语句，每条语句需要以“@$@”结尾，默认第一条语句来自用户，第二条语句是ai的回答，第三条语句
 *      又来自用户，以此类推。
 *      如果在一条语句后不想转换角色，例如用户发完一句话后还想发，那么只要多加一个“@$@”即可（把下一条ai的回答置空）
 * 
 * 下面是这种模式下question的一个示例：
 * “我接下来会问一个问题，然后提供一些参考文本，请结合我提供的参考文本回答我的问题。@$@
 *好的，请提供你的问题。@$@@$@@$@
 *请先介绍一下北京大学，然后再介绍一下北京大学JAVA课程第五组@$@
 *好的，我已经收到你的问题，请提供你的参考文本@$@
 *北京大学JAVA课程一共有六组，其中第五组由5位同学组成，包括2名来自北京大学信息管理系的同学、两名来自北京大学工学院的同学和1名来自北京大学信息科学技术学院的同学。
 *@$@”
 *
 * 在上面这个示例中，“@$@@$@@$@”连续3组分隔符和1组分隔符的效果相同（因为角色转换3次=转换1次）。
 * 这个示例的效果与has_reference=="yes"里提供的question+reference示例效果完全一致，请自由选择合适的模式。
 */