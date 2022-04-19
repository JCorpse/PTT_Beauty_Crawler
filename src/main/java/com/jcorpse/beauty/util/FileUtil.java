package com.jcorpse.beauty.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileUtil {

    public static void DirMaker(String Filepath) {
        String[] SplitFilepath = Filepath.split("/");
        Filepath = new String();
        for (String temp : SplitFilepath) {
            Filepath += temp;
            File C_Dir = new File(Filepath);
            if (C_Dir.mkdir()) {
                log.info("新建[" + temp + "]資料夾");
            }
            Filepath += "/";
        }
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }
}
