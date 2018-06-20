package com.solomon.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @项目名称： MobileGuard
 * @包名：com.solomon.mobileguard.utils
 * @类描述： 存储设置中心的数据
 * @作者： Administrator
 * @创建时间： 2018/6/20 0020 23:12
 */
public class SpUtils {
    private static SharedPreferences sp;

    /**
     * 写boolean数据
     * @param ctx 上下文环境
     * @param key 存储节点名称
     * @param value 存储节点的值
     */
    public static void putBoolean(Context ctx, String key, boolean value){
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 读boolean数据
     * @param ctx 上下文环境
     * @param key 存储节点名称
     * @param defaultValue 默认值
     * @return 存储节点的值
     */
    public static boolean getBoolean(Context ctx, String key, boolean defaultValue){
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        return sp.getBoolean(key, defaultValue);
    }
}
