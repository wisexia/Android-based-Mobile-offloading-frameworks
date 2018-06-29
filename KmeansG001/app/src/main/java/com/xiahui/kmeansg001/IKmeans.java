package com.xiahui.kmeansg001;

import android.graphics.Bitmap;

/**
 * Created by XH on 2017/5/22.
 * Kmeans算法接口
 */
public interface IKmeans {
    Bitmap getResultGraphics(Bitmap data, int k, int m);
}
