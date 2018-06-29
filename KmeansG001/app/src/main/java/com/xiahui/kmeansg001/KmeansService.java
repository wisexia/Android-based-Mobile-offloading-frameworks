package com.xiahui.kmeansg001;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

public class KmeansService extends Service {

    private IBinder iBinder = null;

    @Override
    public void onCreate() {
        iBinder = new KmeansBinder(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * 接口方法的实现
     */
    public class KmeansBinder extends KmeansStub {
        private Context cx;

        private final int[] RGB_INIT = {0xFFFFFF, 0x000000, 0xEE9611, 0x808080, 0xFFFF00, 0xFFFFFF};
        private final int[] RGB_FILL = {android.graphics.Color.WHITE, android.graphics.Color.BLACK,
                android.graphics.Color.BLUE, android.graphics.Color.GRAY};

        public KmeansBinder(Context cx) {
            this.cx = cx;
        }

        @Override
        public Bitmap getResultGraphics(Bitmap bitmapData, int k, int m) {

            if (bitmapData == null) {
                return null;
            }

            int imageWidth = bitmapData.getWidth();
            int imageHeigth = bitmapData.getHeight();

            int[][] data = new int[imageWidth][imageHeigth];
            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeigth; j++) {
                    data[i][j] = bitmapData.getPixel(i, j);
                }
            }

            int[][] result = ImageSegmentation(data, k, m);

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeigth; j++) {
                    int category = result[i][j];
                    if (category != 0 && category != 1) {
                        Log.i("TAG", category + " is error.");
                    }
                    bitmapData.setPixel(i, j, RGB_FILL[category]);
                }
            }

            return bitmapData;
        }

        private int[][] ImageSegmentation(int[][] data, int k, int m) {

            int imageHeight = data[0].length;
            int imageWidth = data.length;
            int[][] classify = new int[imageWidth][imageHeight];

            java.util.ArrayList<Integer> dataCenter = new java.util.ArrayList<>();
            java.util.ArrayList<java.util.ArrayList<Integer>> cluster = new java.util.ArrayList<>();

            for (int i = 0; i < k; i++) {
                Integer pixel = new Integer(RGB_INIT[i]);
                dataCenter.add(pixel);
            }

            for (int i = 0; i < k; i++) {
                java.util.ArrayList<Integer> pixels = new java.util.ArrayList<>();
                pixels.add(dataCenter.get(i));
                cluster.add(pixels);
            }

            for (int kk = 0; kk < m; kk++) {

                for (int i = 0; i < k; i++) {
                    if (cluster.get(i) != null) {
                        cluster.get(i).clear();
                    }
                }

                for (int i = 0; i < imageWidth; i++) {
                    for (int j = 0; j < imageHeight; j++) {
                        Integer pixel = new Integer(data[i][j]);

                        int category = 0;
                        double minDistance = Double.POSITIVE_INFINITY;
                        for (int ii = 0; ii < k; ii++) {
                            Integer pixelCenter = dataCenter.get(ii);
                            int xr = (pixel & 0xFF0000) >> 16;
                            int xg = (pixel & 0x00FF00) >> 8;
                            int xb = (pixel & 0x0000FF);

                            int yr = (pixelCenter & 0xFF0000) >> 16;
                            int yg = (pixelCenter & 0x00FF00) >> 8;
                            int yb = (pixelCenter & 0x0000FF);

                            double distance = Math.sqrt((xr - yr) * (xr - yr) + (xg - yg) * (xg - yg) + (xb - yb) * (xb - yb));
                            if (minDistance > distance) {
                                category = ii;
                                minDistance = distance;
                            }
                        }

                        classify[i][j] = category;

                        cluster.get(category).add(pixel);
                    }
                }

                for (int i = 0; i < k; i++) {
                    long rSum = 0;
                    long gSum = 0;
                    long bSum = 0;
                    java.util.ArrayList<Integer> rowPixel = cluster.get(i);
                    for (int j = 0; j < rowPixel.size(); j++) {
                        rSum += ((rowPixel.get(j) & 0xFF0000) >> 16);
                        gSum += ((rowPixel.get(j) & 0x00FF00) >> 8);
                        bSum += rowPixel.get(j) & 0x0000FF;
                    }

                    int r = (int) rSum / rowPixel.size();
                    int g = (int) gSum / rowPixel.size();
                    int b = (int) bSum / rowPixel.size();
                    dataCenter.set(i, new Integer(((r << 16) & 0xFF0000) + ((g << 8) & 0x00FFF00) + (b & 0x0000FF)));
                }
            }

            return classify;
        }
    }
}
