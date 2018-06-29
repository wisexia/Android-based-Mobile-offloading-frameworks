package com.xiahui.kmeansg001;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by XH on 2017/5/22.
 */

public abstract class KmeansStub extends Binder implements IKmeans {
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        Bitmap bitmapData = data.readParcelable(null);
        int k = data.readInt();
        int m = data.readInt();
        reply.writeParcelable(getResultGraphics(bitmapData, k, m), 0);
        return true;
    }
}
