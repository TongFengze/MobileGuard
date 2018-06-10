package com.solomon.mobileguard.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.solomon.mobileguard.R;
import com.solomon.mobileguard.utils.StreamUtil;
import com.solomon.mobileguard.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final String tag = "SplashActivity";
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int IO_ERROR = 102;
    private static final int JSON_ERROR = 103;

    private TextView tv_versionName;
    private int mLocalVersionCode;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_VERSION:
                    //弹出对话框
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case IO_ERROR:
                    enterHome();
                    ToastUtil.showToast(getApplicationContext(), "读取异常");
                    break;
                case JSON_ERROR:
                    enterHome();
                    ToastUtil.showToast(getApplicationContext(), "Json解析异常");
                    break;
            }
        }
    };

    private void showUpdateDialog() {
        //对话框是依赖Activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setIcon(R.mipmap.ic_launcher);

        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();
    }

    private void initUI() {
        tv_versionName = findViewById(R.id.tv_versionname);
    }

    private void initData() {
        //1.获取本地版本名称
        tv_versionName.setText("版本名称" + getVersionName());
        //检查是否需要更新
        //2.获取本地版本号
        mLocalVersionCode = getLocalVersionCode();
        //3.获取服务端后台版本号
        checkVersion();
    }

    private void checkVersion() {
        new Thread(){
            public void run(){
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();

                try {
                    URL url = new URL("http://192.168.1.2:8080/update.json");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setReadTimeout(2000);
                    //允许输入流
                    httpURLConnection.setDoInput(true);
                    //设置连接方式为GET，默认就是get请求方式
                    httpURLConnection.setRequestMethod("GET");
                    int responseCode = httpURLConnection.getResponseCode();
                    // 状态码等于200，表示正确
                    if (responseCode == 200) {
                        InputStream is = httpURLConnection.getInputStream();
                        String json = StreamUtil.stream2String(is);
                        Log.i(tag, json);
                        //解析json
                        JSONObject jsonObject = new JSONObject(json);
                        if(mLocalVersionCode < Integer.parseInt(jsonObject.getString("versionCode"))){
                            msg.what = UPDATE_VERSION;
                        }else{
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (IOException e) {
                    msg.what = IO_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    //指定睡眠时间，最多4s
                    long endTime = System.currentTimeMillis();
                    if(endTime - startTime < 4000){
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 获取本地版本名称
     * @return 本地版本名称
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try{
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
   }

    /**
     * 获取本地版本号
     * @return 本地版本号
     */
    private int getLocalVersionCode() {
        PackageManager pm = getPackageManager();
        try{
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
