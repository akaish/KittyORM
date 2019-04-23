package net.akaish.kittyormdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

public class DemoPermissions {

    public static final String READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @TargetApi(23)
    public static final boolean isPermissionGranted(Context ctx, String permissionName) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return PackageManager.PERMISSION_GRANTED == ctx.checkSelfPermission(permissionName);
        else
            return ContextCompat.checkSelfPermission(ctx, permissionName) == PackageManager.PERMISSION_GRANTED;
    }
}
