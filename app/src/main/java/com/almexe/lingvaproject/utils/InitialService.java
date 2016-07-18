package com.almexe.lingvaproject.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.db.GetDataFromDb;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class InitialService extends Service{

    final String LOG_TAG = "myLogs";
    protected String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new GetDataFromDb().execute();
                if(VKSdk.isLoggedIn())
                    VKSdk.login(new Driver(), scope);
                else{
                    new VkErrorResponse().execute();
                }
            }
        }).start();
    }
}
