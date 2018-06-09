package com.solomon.mobileguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    /**
     * 流转换成字符串
     * @param is    流对象
     * @return      返回转换后的字符串，null代表异常
     */
    public static String stream2String(InputStream is){
        //1.在读取的过程中，将读取的内容存储在缓存中，然后一次性的转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        //2.读流操作，读到没有为止
        try{
            while((temp = is.read(buffer)) != -1) {
                bos.write(buffer, 0, temp);
            }
            return bos.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
