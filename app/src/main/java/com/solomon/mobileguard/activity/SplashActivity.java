package com.solomon.mobileguard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.solomon.mobileguard.R;
import com.solomon.mobileguard.utils.ConstantValue;
import com.solomon.mobileguard.utils.SpUtils;
import com.solomon.mobileguard.utils.StreamUtil;
import com.solomon.mobileguard.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int IO_ERROR = 102;
    private static final int JSON_ERROR = 103;

    private TextView tv_versionName;
    private LinearLayout  ll_layout;
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "OnClick");
                //下载新版本的apk
                //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MobileGuard.apk";
                RequestParams params = new RequestParams(mDownloadUrl);
                params.setSaveFilePath(path);
                //自动为文件命名
                params.setAutoRename(true);
                x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        //apk下载完成后，调用系统的安装方法
                        Log.e(TAG, "下载成功");
                        installApk(result);
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.e(TAG, "下载失败");
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                    }
                    //网络请求之前回调
                    @Override
                    public void onWaiting() {
                    }
                    //网络请求开始的时候回调
                    @Override
                    public void onStarted() {
                        Log.e(TAG, "开始下载");
                    }
                    //下载的时候不断回调的方法
                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        //当前进度和文件总大小
                        Log.e(TAG, "下载进度:" + current + "/" + total);
                    }
                });
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //确保点击返回的时候也能进入主界面
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 调用系统方法安装apk
     * @param file
     */
    private void installApk(File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //用startActivityForResult方法来监听取消安装事件
        startActivityForResult(intent, 0);
    }

    /**
     * 取消安装后的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
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
        //初始化动画
        initAnimation();
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        ll_layout.setAnimation(alphaAnimation);
    }

    private void initUI() {
        tv_versionName = findViewById(R.id.tv_versionname);
        ll_layout = (LinearLayout)findViewById(R.id.ll_rootLayout);
    }

    private void initData() {
        //1.获取本地版本名称
        tv_versionName.setText("版本名称" + getVersionName());
        //检查是否需要更新
        //2.获取本地版本号
        mLocalVersionCode = getLocalVersionCode();
        //3.获取服务端后台版本号

        if(SpUtils.getBoolean(this, ConstantValue.AUTOUPDATE, false)) {
            checkVersion();
        }
        else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }

    /**
     * 与后台版本对比
     */
    private void checkVersion() {
        new Thread(){
            public void run(){
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();

                try {
                    URL url = new URL("http://192.168.1.3:8080/update.json");
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
                        Log.i(TAG, json);
                        //解析json
                        JSONObject jsonObject = new JSONObject(json);
                        mVersionDes = jsonObject.getString("versionDes");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

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
