package com.solomon.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.solomon.mobileguard.R;

/**
 * @项目名称： MobileGuard
 * @包名：com.solomon.mobileguard.view
 * @类描述：
 * @作者： Administrator
 * @创建时间： 2018/6/18 0018 23:33
 */
public class SettingItemView extends RelativeLayout {
    private CheckBox cb_select;
    private TextView tv_des;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        View view = View.inflate(context, R.layout.setting_item_view, this);
        cb_select = findViewById(R.id.cb_check);
        tv_des = findViewById(R.id.tv_des);
    }

    public boolean isChecked() {
        return cb_select.isChecked();
    }

    public void setChecked(boolean checked) {
        cb_select.setChecked(checked);
        tv_des.setText(checked ? "自动更新已开启" : "自动更新已关闭");
    }
}
