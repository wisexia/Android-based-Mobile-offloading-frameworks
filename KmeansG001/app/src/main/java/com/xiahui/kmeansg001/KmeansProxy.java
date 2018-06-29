package com.xiahui.kmeansg001;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.xiahui.util.IDecision;
import com.xiahui.util.XBinder;

/**
 * Created by XH on 2017/5/22.
 */

public class KmeansProxy implements IKmeans {

    private static final String TAG = "KmeansProxy";
    private XBinder xBinder;

    public KmeansProxy(IBinder iBinder, IDecision iDecision) {
        this.xBinder = new XBinder(iBinder, iDecision);
    }

    @Override
    public Bitmap getResultGraphics(Bitmap bitmapData, int k, int m) {
        Parcel data = Parcel.obtain();      // 存储要执行的数据
        Parcel reply = Parcel.obtain();     // 存储执行后的结果

        // 写入数据
        data.writeParcelable(bitmapData, 0);
        data.writeInt(k);
        data.writeInt(m);
        try {
            xBinder.transact(1, data, reply, 0);
            return reply.readParcelable(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
