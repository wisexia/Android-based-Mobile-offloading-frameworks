package com.xiahui.util;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by XH on 2017/3/9.
 */

public class DecisionProxy implements IDecision {

    private final static String TAG = "DecisionProxy";

    private IBinder iBinder;

    public DecisionProxy(IBinder iBinder) {
        this.iBinder = iBinder;
    }

    @Override
    public boolean makeDecision(long serviceExecuteTime, long cloudExecuteTime, long transmissionTime) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        // 写入数据
        data.writeLong(serviceExecuteTime);
        data.writeLong(cloudExecuteTime);
        data.writeLong(transmissionTime);
        try {
            iBinder.transact(1, data, reply, 0);
            return (reply.readByte() != 0);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
