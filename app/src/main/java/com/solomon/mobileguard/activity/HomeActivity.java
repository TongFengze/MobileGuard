package com.solomon.mobileguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.solomon.mobileguard.R;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    private String[] mTitleNames;
    private int[] mTitleImageIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initUI() {
        gv_home = (GridView)findViewById(R.id.gv_home);
    }

    private void initData() {
        //准备数据（文字和图片9组）
        mTitleNames = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mTitleImageIds = new int[]{
                R.mipmap.shoujifangdaonormal,
                R.mipmap.tongxunweishinormal,
                R.mipmap.ruanjianguanjianormal,
                R.mipmap.jinchengguanlinormal,
                R.mipmap.liuliangtongjinormal,
                R.mipmap.shoujishadunormal,
                R.mipmap.huancunqinglinormal,
                R.mipmap.gaojigongjunormal,
                R.mipmap.shezhizhongxinnormal
        };

        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        break;

                    case 8:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleNames.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);

            tv_title.setText(mTitleNames[position]);
            iv_icon.setImageResource(mTitleImageIds[position]);

            return view;
        }
    }
}
