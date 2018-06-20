package com.solomon.mobileguard.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.solomon.mobileguard.R;
import com.solomon.mobileguard.view.SettingItemView;

/**
 * @项目名称： MobileGuard
 * @包名：com.solomon.mobileguard.activity
 * @类描述：
 * @作者： Administrator
 * @创建时间： 2018/6/18 0018 22:39
 */
public class SettingActivity extends AppCompatActivity{
    private SettingItemView siv_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        siv_update = findViewById(R.id.siv_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siv_update.setChecked(!siv_update.isChecked());
            }
        });
    }
}
