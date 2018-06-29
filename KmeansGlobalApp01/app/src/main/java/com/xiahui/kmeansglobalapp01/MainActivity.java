package com.xiahui.kmeansglobalapp01;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xiahui.util.BindDecision;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    ImageView imageView;    // 显示选择或处理后的图片
    Button btn_kmeans;      // 执行Kmeans算法的Button
    Button btn_chooseImage;     // 选择图片的Button
    EditText et_k;      // 编辑Kmeans算法的分类数
    EditText et_m;      // 编辑Kmeans算法的迭代数
    EditText et_exectutionTime;     // 显示执行Kmeans算法消耗的时间

    private int k;  // Kmeans算法的分类数
    private int m;  // Kmeans算法的迭代数
    private Bitmap bitmapData = null;   // Bitmap存储图片数据

    private IKmeans iKmeans = null;     // IBinder 接口
    PermissionHelper permissionHelper = new PermissionHelper();     // 权限检查

    private BindDecision bindDecision = new BindDecision(MainActivity.this);    // 绑定远程决策服务相关操作类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();     // 初始化布局
        bindService();  // 绑定服务

        bindDecision.bindService();     // 绑定决策App
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iKmeans = new KmeansProxy(service, bindDecision.getProxy());     // 服务绑定时，获取IKmeans接口数据
            Log.e(TAG, "iKmeans: " + iKmeans);
            Log.e(TAG, "iDecision: " + bindDecision.getProxy());
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            iKmeans = null;
        }
    };

    /** 绑定本地Service */
    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiahui.kmeansglobalapp01", "com.xiahui.kmeansglobalapp01.KmeansService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /** 初始化布局 */
    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        btn_chooseImage = (Button) findViewById(R.id.btn_chooseImage);
        btn_kmeans = (Button) findViewById(R.id.btn_kmeans);
        et_exectutionTime = (EditText) findViewById(R.id.ed_executionTime);
        et_k = (EditText) findViewById(R.id.et_classifyNum);
        et_m = (EditText) findViewById(R.id.ed_iteratorNum);

        btn_chooseImage.setOnClickListener(this);
        btn_kmeans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chooseImage:
                chooseImageFromFile();      // 选择图片
                break;
            case R.id.btn_kmeans:
                et_exectutionTime.setText("0 ms");
                k = Integer.parseInt(et_k.getText().toString());
                m = Integer.parseInt(et_m.getText().toString());
                /* 获取读写权限 */
                if (permissionHelper.checkSDcardReadPermission(this) && permissionHelper.checkSDcardWritePermission(this)) {
                    long startTime = System.nanoTime();     // 获取开始时间
                    bitmapData = iKmeans.getResultGraphics(bitmapData, k, m);   // 利用IBinder接口调用实际执行的方法
                    long endTime = System.nanoTime();       // 获取结束时间
                    et_exectutionTime.setText((endTime - startTime) / 1000000 + " ms");
                    imageView.setImageBitmap(bitmapData);
                    break;
                }
        }
    }

    /** 选择图片 */
    private void chooseImageFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                ContentResolver contentResolver = getContentResolver();
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    bitmapData = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);   // 获取选择图片的Bitmap
                    imageView.setImageBitmap(bitmapData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
