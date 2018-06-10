package com.solomon.mobileguard.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * 弹框消息
     * @param ctx 上下文
     * @param msg 打印消息
     */
    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
