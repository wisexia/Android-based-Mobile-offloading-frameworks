package com.xiahui.kmeansg001;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Dell on 2017/5/11.
 */

public class PermissionHelper extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 1; //权限请求码

    /**
     * 检查读入权限
     * */
    public boolean checkSDcardReadPermission(Context context){
        if (!checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //请求读权限
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 检查写入权限
     * */
    public boolean checkSDcardWritePermission(Context context){
        if (!checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //请求写权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
        return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 检测权限是否授权
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //得到了授权
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    //未授权
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}




