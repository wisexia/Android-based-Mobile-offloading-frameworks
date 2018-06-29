package com.xiahui.kmeansdecision;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class DecisionService extends Service {

    private final static String TAG = "DecisionService";

    private IBinder iBinder = null;

    @Override
    public void onCreate() {
        iBinder = new DecisionBinder(getApplicationContext());
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * 接口方法的实现
     */
    public class DecisionBinder extends DecisionStub {
        private Context context;
        public DecisionBinder(Context context) {
            this.context = context;
        }

        @Override
        public boolean makeDecision(long serviceExecuteTime, long totalCloudExecuteTime, long cloudExecuteTime) {
            if(isWifiConnected()) {
                return energySaving(serviceExecuteTime, totalCloudExecuteTime, cloudExecuteTime, 0);
            }else {
                return false;
            }
        }

        /**
         * 比较计算出的实际节省能量值与standardEnergySaving
         * @param serviceExecuteTime 本地执行所需时间，单位ms
         * @param cloudExecuteTime 远程执行所需时间，单位ms
         * @param transmissionTime 网络传输所需时间，单位ms
         * @param standardEnergySaving 标准能量节省值
         * @return 实际节省能量不小于standardEnergySaving则返回true,否则返回false
         */
        private boolean energySaving(long serviceExecuteTime, long cloudExecuteTime, long transmissionTime, int standardEnergySaving) {

            long Pc = 3000000;    // 本地执行时的功率，单位uW
            long Pi = 600000;    // 本地空闲时的功率，单位uW
            long Ptr = 1800000;   // 网络通信时的功率，单位uW

            Log.e(TAG, "serviceExecuteTime = " + serviceExecuteTime + "  cloudExecuteTime = " + cloudExecuteTime + "  transmissionTime = " + transmissionTime);
            long ServiceEnergy = Pc * serviceExecuteTime / 1000000;      // 本地执行需要消耗的能量，单位mJ
            long CloudEnergy = (Pi * cloudExecuteTime + Ptr * transmissionTime) / 1000000;        // 云端执行需要消耗的能量，单位mJ
            long energySaving = ServiceEnergy - CloudEnergy;     // 实际节省的能量，单位J
            Log.e(TAG, "ServiceEnergy = " + ServiceEnergy + "  CloudEnergy = " + CloudEnergy + "  energySaving = " + energySaving);
            return true;
/*        if(energySaving >= standardEnergySaving) {
            return true;
        }else {
            return false;
        }*/
        }

        /** 判断wifi是否连接 */
        private boolean isWifiConnected() {
            if(context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
                if(info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return info.isAvailable();
                }
            }
            return false;
        }
    }
}
