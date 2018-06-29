package com.xiahui.kmeansdecision;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiahui.kmeansdecision", "com.xiahui.kmeansdecision.DecisionService"));
        startService(intent);       // 启动远程服务
    }
}
