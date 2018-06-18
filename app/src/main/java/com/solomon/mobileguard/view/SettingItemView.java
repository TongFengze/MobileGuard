package com.solomon.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.solomon.mobileguard.R;

/**
 * @项目名称： MobileGuard
 * @包名：com.solomon.mobileguard.view
 * @类描述：
 * @作者： Administrator
 * @创建时间： 2018/6/18 0018 23:33
 */
public class SettingItemView extends RelativeLayout {

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
    }
}
