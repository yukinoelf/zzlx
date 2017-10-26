package com.zhizulx.tt.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by yuki on 2017/1/19.
 */

public class ZipUtil {
    static int k = 1; // 定义递归次数变量
    public ZipUtil() {}


    /**
     * 压缩指定的单个或多个文件，如果是目录，则遍历目录下所有文件进行压缩
     * @param zipFileName ZIP文件名包含全路径
     * @param files  文件列表
     */
    public static boolean zip(String zipFileName, File... files) {
        ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
            for (int i = 0; i < files.length; i++) {
                if (null != files[i]) {
                    zip(out, files[i], files[i].getName());
                }
            }
            out.close(); // 输出流关闭
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行压缩
     * @param out ZIP输入流
     * @param f   被压缩的文件
     * @param base  被压缩的文件名
     */
    private static void zip(ZipOutputStream out, File f, String base) { // 方法重载
        try {
            if (f.isDirectory()) {//压缩目录
                try {
                    File[] fl = f.listFiles();
                    if (fl.length == 0) {
                        out.putNextEntry(new ZipEntry(base + "/"));  // 创建zip实体
                    }
                    for (int i = 0; i < fl.length; i++) {
                        zip(out, fl[i], base + "/" + fl[i].getName()); // 递归遍历子文件夹
                    }
                    //System.out.println("第" + k + "次递归");
                    k++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{ //压缩单个文件
                out.putNextEntry(new ZipEntry(base)); // 创建zip实体
                FileInputStream in = new FileInputStream(f);
                BufferedInputStream bi = new BufferedInputStream(in);
                int b;
                while ((b = bi.read()) != -1) {
                    out.write(b); // 将字节流写入当前zip目录
                }
                out.closeEntry(); //关闭zip实体
                in.close(); // 输入流关闭
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
