package com.robot.silentinstallerhelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by lny on 2018/1/23.
 */

public class SilentInstallHelper {
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

    private Context context;
    private BroadcastReceiver receiver;

    public SilentInstallHelper(Context context) {
        this.context = context;
    }

    public void install(String packageName,String path){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(package_name,
                clazz_name));
        intent.putExtra(key_path,path);
        intent.putExtra(key_packagename,packageName);
        intent.putExtra(key_commond,commond_install);
        context.startService(intent);
    }

    public void uninstall(String packageName){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(package_name,
                clazz_name));
        intent.putExtra(key_packagename,packageName);
        intent.putExtra(key_commond,commond_uninstall);
        context.startService(intent);
    }



    public void register(String aciton){
        IntentFilter filter = new IntentFilter(aciton);
        context.registerReceiver(receiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = intent.getIntExtra(key_result,-1);
                int command = intent.getIntExtra(key_commond,-1);
                if (command==commond_install){
                    switch (result){
                        case result_fail:
                            String msg = intent.getStringExtra(key_msg);
                            if (onInstallListener!=null){
                                onInstallListener.onFail(msg);
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

                            String msg = intent.getStringExtra(key_msg);
                            if (onUninstallListener!=null){
                                onUninstallListener.onFail(msg);
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
        void onFail(String msg);
        void onSuccess();
    }

    private OnUninstallListener onUninstallListener;

    public com.robot.silentinstallerhelper.SilentInstallHelper.OnUninstallListener getOnUninstallListener() {
        return onUninstallListener;
    }

    public void setOnUninstallListener(com.robot.silentinstallerhelper.SilentInstallHelper.OnUninstallListener onUninstallListener) {
        this.onUninstallListener = onUninstallListener;
    }

    public interface  OnUninstallListener{
        void onFail(String msg);
        void onSuccess();
    }
}
