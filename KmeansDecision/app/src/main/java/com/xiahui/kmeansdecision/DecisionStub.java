package com.xiahui.kmeansdecision;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by XH on 2017/3/23.
 */

public abstract class DecisionStub extends Binder implements IDecision {
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        reply.writeByte((byte) (this.makeDecision(data.readLong(), data.readLong(), data.readLong())?1: 0));
        return true;
    }
}
