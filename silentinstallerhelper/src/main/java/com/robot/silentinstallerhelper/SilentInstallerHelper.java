package com.robot.silentinstallerhelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

/**
 * Created by lny on 2018/1/23.
 */

public class SilentInstallerHelper {
    public static final String package_name = "com.robot.installer";
    public static final String clazz_name = "com.robot.installer.service.InstallService";


    public static final String key_result = "result";
    public static final int result_success=1;
    public static final int result_fail = 2;
    public static final int commond_install = 3;
    public static final int commond_uninstall = 4;
    public static final String key_commond = "commond";
    public static final String key_path = "path";
    public static final String key_msg="msg";
    public static final String key_packagename = "packagename";
    public static final String key_appkey = "appkey";

    public static final int error_unknow = -1;
    public static final int error_parse = 1;
    public static final int error_install = 2;
    public static final int error_filetype = 3;
    public static final int error_notexists = 4;

    public static final String key_errorcode = "errorcode";

    private Context context;
    private BroadcastReceiver receiver;
    private String appkey;

    public SilentInstallerHelper(@NonNull Context context, @NonNull String appkey) {
        this.context = context;
        this.appkey = appkey;
    }

    public void install(@NonNull String path){

        File file = new File(path);
        if (file.exists()){
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = null;
            if (info != null) {
                appInfo = info.applicationInfo;
                String packageName = appInfo.packageName;
//                Log.d("xxxxx","packagename : "+packageName);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(package_name,
                        clazz_name));
                intent.putExtra(key_path,path);
                intent.putExtra(key_commond,commond_install);
                intent.putExtra(key_appkey,appkey);
                intent.putExtra(key_packagename,packageName);
                context.startService(intent);
            }else{
                if (onInstallListener!=null){
                    onInstallListener.onFail(error_parse);
                }
            }
        }else{
            if (onInstallListener!=null){
                onInstallListener.onFail(error_notexists);
            }
        }


    }

    public void uninstall(String packageName){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(package_name,
                clazz_name));
        intent.putExtra(key_packagename,packageName);
        intent.putExtra(key_commond,commond_uninstall);
        intent.putExtra(key_appkey,appkey);
        context.startService(intent);
    }



    public void register(){
        IntentFilter filter = new IntentFilter(appkey);
        context.registerReceiver(receiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = intent.getIntExtra(key_result,-1);
                int command = intent.getIntExtra(key_commond,-1);
                if (command==commond_install){
                    switch (result){
                        case result_fail:
                            int code = intent.getIntExtra(key_errorcode,error_unknow);
                            if (onInstallListener!=null){
                                onInstallListener.onFail(code);
                            }
                            break;
                        case result_success:
                            if (onInstallListener!=null){
                                onInstallListener.onSuccess();
                            }
                            break;
                    }
                }else if (command == commond_uninstall){
                    switch (result){
                        case result_fail:

                            int code = intent.getIntExtra(key_errorcode,error_unknow);
                            if (onUninstallListener!=null){
                                onUninstallListener.onFail(code);
                            }
                            break;
                        case result_success:
                            if (onUninstallListener!=null){
                                onUninstallListener.onSuccess();
                            }
                            break;
                    }
                }


            }
        },filter);
    }
    public void unregister(){
        if (receiver!=null){
            context.unregisterReceiver(receiver);
        }
        onInstallListener = null;
    }

    private OnInstallListener onInstallListener;

    public OnInstallListener getOnInstallListener() {
        return onInstallListener;
    }

    public void setOnInstallListener(OnInstallListener onInstallListener) {
        this.onInstallListener = onInstallListener;
    }

    public interface  OnInstallListener{
        void onFail(int code);
        void onSuccess();
    }

    private OnUninstallListener onUninstallListener;

    public SilentInstallerHelper.OnUninstallListener getOnUninstallListener() {
        return onUninstallListener;
    }

    public void setOnUninstallListener(SilentInstallerHelper.OnUninstallListener onUninstallListener) {
        this.onUninstallListener = onUninstallListener;
    }

    public interface  OnUninstallListener{
        void onFail(int code);
        void onSuccess();
    }
}
