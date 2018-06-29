package com.xiahui.util;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by XH on 2017/3/23.
 * 实现IBinder接口，覆写transact方法
 */
public class XBinder implements IBinder {

    private final static String TAG = "XBinder";

    private static long serviceExecuteTime = 0;  // 本地执行所需时间，单位ms
    private static long cloudExecuteTime = 970;     // 远程执行所需时间，单位ms
    private static long totalCloudExecuteTime = 2788 * 2 - cloudExecuteTime;   // 云端执行所需总时间，单位ms
    private static long transmissionTime = totalCloudExecuteTime - cloudExecuteTime;    // 网络传输所需时间，单位ms

    private IBinder iBinder = null;
    private IDecision iDecision = null;     // 决策App代理接口

    public XBinder(IBinder iBinder,IDecision iDecision) {
        this.iBinder = iBinder;
        this.iDecision = iDecision;
    }

    private void printEnergy() {

        long Pc = 3000000;    // 本地执行时的功率，单位uW
        long Pi = 600000;    // 本地空闲时的功率，单位uW
        long Ptr = 1800000;   // 网络通信时的功率，单位uW

        Log.e(TAG, "totalCloudExecuteTime = " + totalCloudExecuteTime);
        Log.e(TAG, "serviceExecuteTime = " + serviceExecuteTime + "  cloudExecuteTime = " + cloudExecuteTime + "  transmissionTime = " + transmissionTime);
        long ServiceEnergy = Pc * serviceExecuteTime / 1000000;      // 本地执行需要消耗的能量，单位mJ
        long CloudEnergy = (Pi * cloudExecuteTime + Ptr * transmissionTime) / 1000000;        // 云端执行需要消耗的能量，单位mJ
        long energySaving = ServiceEnergy - CloudEnergy;     // 实际节省的能量，单位J
        Log.e(TAG, "ServiceEnergy = " + ServiceEnergy + "  CloudEnergy = " + CloudEnergy + "  energySaving = " + energySaving);
    }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

        data.setDataPosition(0);
        Utils conn = new Utils(data);
        long startTime = System.nanoTime();     // 获取开始时间
        boolean temp = iDecision.makeDecision(serviceExecuteTime, cloudExecuteTime, transmissionTime);    // 判定是否卸载
        long endTime = System.nanoTime();       // 获取结束时间
        Log.e(TAG, "makeDecisionTime = " + (endTime - startTime) / 1000000);
        printEnergy();
        if(temp) {
            /** 数据传递到云端执行 */
            Log.e(TAG, "Cloud Execute !");
            startTime = System.nanoTime();     // 获取开始时间
            ExecutorService pool = Executors.newFixedThreadPool(3);     // 开辟线程池
            try {
                Bitmap bitmap = pool.submit(conn).get();
                reply.writeParcelable(bitmap, 0);     // 云端执行结果写入reply
                reply.setDataPosition(0);       // reply指针位置归0
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            pool.shutdown();        // 关闭线程池
            endTime = System.nanoTime();       // 获取结束时间
            Log.e(TAG, "totalCloudExecuteTime = " + (endTime - startTime) / 1000000);
        } else {
            /** 调用本地Service的方法 */
            Log.e(TAG, "Service Execute !");
            startTime = System.nanoTime();     // 获取开始时间
            data.setDataPosition(0);
            iBinder.transact(code, data, reply, flags);
            endTime = System.nanoTime();       // 获取结束时间
            serviceExecuteTime = (endTime - startTime) / 1000000;
            Log.e(TAG, "serviceExecuteTime = " + serviceExecuteTime);
        }
        return true;
    }

    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return iBinder.getInterfaceDescriptor();
    }
    @Override
    public boolean pingBinder() {
        return iBinder.pingBinder();
    }
    @Override
    public boolean isBinderAlive() {
        return iBinder.isBinderAlive();
    }
    @Override
    public IInterface queryLocalInterface(String descriptor) {
        return iBinder.queryLocalInterface(descriptor);
    }
    @Override
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        iBinder.dump(fd, args);
    }
    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        iBinder.dumpAsync(fd, args);
    }
    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {
        iBinder.linkToDeath(recipient, flags);
    }
    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return iBinder.unlinkToDeath(recipient, flags);
    }
}
