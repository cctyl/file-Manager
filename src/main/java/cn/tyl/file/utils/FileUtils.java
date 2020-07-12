package cn.tyl.file.utils;

import java.io.File;

public class FileUtils {

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {

        File file = new File(filePath);

        boolean result = false;
        if (file.exists()) {

            if (file.isDirectory()) {
                result =   deleteDir(file);

            } else {
                result = file.delete();
            }

        }
        return result;
    }


    /**
     * 删除文件夹
     *
     * @param directory
     */
    private static boolean deleteDir(File directory) {

        File[] files = directory.listFiles();
        if (files == null) {
           return directory.delete();
        } else {
            for (int i = 0; i < files.length; i++) {
                deleteDir(new File(files[i].getAbsolutePath()));
            }
           return directory.delete();
        }

    }

}
