package com.robot.installhelper;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.robot.silentinstallerhelper.SilentInstallerHelper;

public class MainActivity extends AppCompatActivity {

    private SilentInstallerHelper installerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        installerHelper = new SilentInstallerHelper(this,"com.robot.installhelper");
        installerHelper.register();
        installerHelper.setOnInstallListener(new SilentInstallerHelper.OnInstallListener() {
            @Override
            public void onFail(int code) {
                Log.d("xxxxx","install error : "+code);
            }

            @Override
            public void onSuccess() {
                Log.d("xxxxx","install success");
            }
        });

        installerHelper.setOnUninstallListener(new SilentInstallerHelper.OnUninstallListener() {
            @Override
            public void onFail(int code) {
                Log.d("xxxxx","uninstall error : "+code);
            }

            @Override
            public void onSuccess() {
                Log.d("xxxxx","uninstall success");
            }
        });
    }

    public void install(View view){
        installerHelper.install(Environment.getExternalStorageDirectory().getAbsolutePath()+"/bdspeechdemo.apk");
    }

    public void uninstall(View view){
        installerHelper.uninstall("com.baidu.speech.recognizerdemo");
    }

    @Override
    protected void onDestroy() {
        if (installerHelper!=null){
            installerHelper.unregister();
        }
        super.onDestroy();
    }
}
