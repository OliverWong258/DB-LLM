package com.example.demo.util;

import java.io.File;

public class DeleteFilesInDirectory {

    /**
     * 删除指定目录下的所有文件和子文件夹
     *
     * @param directoryPath 目标目录的路径
     * @return 如果成功删除所有文件和文件夹，返回true；否则返回false
     */
    public static boolean deleteAllFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // 确保目标路径存在且是一个目录
        if (directory.exists() && directory.isDirectory()) {
            // 获取该目录下的所有文件和子目录
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是子目录，递归删除其中的文件
                        deleteAllFilesInDirectory(file.getAbsolutePath());
                    }
                    // 删除文件或空目录
                    file.delete();
                }
            }
            return true;  // 成功删除
        }
        return false;  // 目录不存在或不是一个有效目录
    }
}
