package com.almexe.lingvaproject.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.io.IOException;

public class InitialService extends Service{

    private static final Object myLock = new Object();

    final String LOG_TAG = "myLogs";
    protected String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    MainDb mainDb;
    MainDbForUser mainDbForUser;

    public class LocalBinder extends Binder {
        public InitialService getService() {
            return InitialService.this;
        }
    }

    public final LocalBinder localBinder = new LocalBinder();

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {

        synchronized (myLock) {
            DataBase mr = new DataBase();
            Thread t1 = new Thread(mr);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CreateTable createTable = new CreateTable();
            Thread t2 = new Thread(createTable);
            t2.start();
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return localBinder;
    }

    public int getCountLearnedWords() {
        MainDbForUser mainDbForUser = new MainDbForUser(Application.getContext());
        return mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED);
    }

    class DataBase implements Runnable {

        @Override
        public void run() {
            mainDb = new MainDb(getApplicationContext());
            try {
                mainDb.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }try {
                mainDb.openDataBase();
            } catch (Exception ioe) {
                throw new Error("Unable to open database");
            }
        }
    }

    class CreateTable implements Runnable {

        @Override
        public void run() {
            if(VKSdk.isLoggedIn())
                VKSdk.login(new Driver(), scope);
            else{
                Tables.setTableMain("defaultuser");
                mainDbForUser = new MainDbForUser(Application.getContext());
                synchronized (this){
                    if(!mainDbForUser.isExists(Tables.getTableMain())) {
                        mainDbForUser.createTable(Tables.getTableMain());
                        mainDbForUser.insert(Tables.getTableMain());
                        while (mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN) != 10) {
                            for (int i = 0; i < 10; i++) {
                                int result = mainDbForUser.getNotLearnedWords(i, Tables.getTableMain());
                                mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                            }
                        }
                    }
                }
            }
        }
    }

}
