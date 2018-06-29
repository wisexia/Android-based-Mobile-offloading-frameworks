package com.xiahui.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by XH on 2017/3/23.
 * 线程操作，连接云端服务器，处理数据并返回相应结果
 */

public class Utils implements Callable<Bitmap>{

    private final static String TAG = "Utils";

    // 接受传递到云端的数据
    private Bitmap bitmap = null;   // 处理的图片
    private int k = 0;  // Kmeans算法 分类数
    private int m = 0;  // Kmeans算法 迭代数
    private String speedValue = null;   // 测试网速时传递的数据
    private String photo = null;    // Base64 编码后的Bitmap 数据

    /**
     * 连接云端的url
     * 云端公网ip：121.201.58.139
     * http默认端口：80
     * 云端执行的Servlet：/execute/ExecuteServlet
     */
    private String url = "http://121.201.58.139/execute/KmeansServlet";
    /**
     * 模拟器连接本地Tomcat服务器的url
     * 模拟器默认本地ip:10.0.2.2
     */
    // private String url = "http://10.0.2.2:8080/execute/KmeansServlet";

    /* 构造函数，接受传递的数据 */
    public Utils(String speedValue) {
        this.speedValue = speedValue;
    }
    public Utils(Parcel data) {
        this.bitmap = data.readParcelable(null);
        this.k = data.readInt();
        this.m = data.readInt();
    }

    /**
     * 利用Base64 编码将Bitmap 转换成String
     * @param bitmap 待编码的Bitmap
     * @return 编码后的String 类型数据
     */
    public String bitmap2StrByBase64(Bitmap bitmap){
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);  // 无压缩编码
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);  // 利用URL_SAFE、NO_WRAP格式编码，目的去除 \ / + \n 等特殊符号
    }

    /**
     * @return 待传输的Bitmap数据大小
     */
    public long getBitmap2StrLength() {
        this.photo = bitmap2StrByBase64(bitmap);
        return photo.length();
    }

    /**
     * 利用Post请求传输数据
     * @return 服务器处理后的数据
     */
    private Bitmap doPost() {

        if(photo == null) {
            photo = bitmap2StrByBase64(bitmap);
        }
        byte[] data = ( "k=" + k + "&m=" + m + "&photo=" + photo+"&speedValue=" + speedValue).getBytes();
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setReadTimeout(5000);      // 设置超时时间
            conn.setRequestMethod("POST");   // 设置请求方式
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();

            /* 获取执行结果 */
            InputStream input = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
            conn.disconnect();
            return bitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Bitmap call() throws Exception {
        return doPost();
    }
}
