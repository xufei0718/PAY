package com.mybank.pc.kits;

import com.jfinal.kit.LogKit;

import java.io.*;
import java.text.DecimalFormat;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.kits]
 * 类名称:    [FileKit]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/30]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class FileKit {

    public static String GetFileSize(File file) {
        String size = "";
        if (file.exists() && file.isFile()) {
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else if (file.exists() && file.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        return size;
    }

    /**
     * 读取文件内容
     * @param fileName
     * @return
     */
    public static String loadFile2Str(String fileName){
        InputStream inputStream = null;
        StringBuilder sb=new StringBuilder();
        try {
            inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(inputStream == null) {
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    inputStream));
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
//            LogKit.debug(sb.toString());
        } catch (FileNotFoundException e) {
            LogKit.error("指定的文件未找到");
        } catch (IOException e) {
            LogKit.error("IO读取错误");
        }finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var11) {
                    LogKit.error(var11.getMessage(), var11);
                }
            }

        }

        return sb.toString();
    }

}
