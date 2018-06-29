package com.xiahui.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by XH on 2017/3/9.
 */

/**
 * 与决策App绑定的相关操作
 */
public class BindDecision {

    private final static String TAG = "BindDecision";

    private IDecision iDecision = null;     // 存放获取到的代理
    private Context context = null;         // 将要绑定的Activity的上下文
    public BindDecision(Context context) {
        this.context = context;
    }

    /* 绑定决策APP */
    public void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiahui.kmeansglobaldecision", "com.xiahui.kmeansglobaldecision.DecisionService"));
        context.bindService(intent, decisionConn, Context.BIND_AUTO_CREATE);
    }

    /* 解除绑定 */
    public void unbindService() {
        context.unbindService(decisionConn);
    }

    private ServiceConnection decisionConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iDecision = new DecisionProxy(iBinder);   // 拿到决策App的代理
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iDecision = null;
        }
    };

    /* 获取代理接口iDecision */
    public IDecision getProxy() {
        return iDecision;
    }
}
