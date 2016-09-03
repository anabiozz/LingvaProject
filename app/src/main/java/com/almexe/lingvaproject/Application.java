package com.almexe.lingvaproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.pages.BaseFragment;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.UpdateInfo;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class Application extends android.app.Application{

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
            @Override
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(Application.this, BaseFragment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            }
        };
        @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    private static Application instance;

    public Application() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    private final static String UPDATE_INFO_PREFS = "UPDATE_INFO_PREFS";
    private final static String UPDATE_INFO_MAIN_TEXT_VIEW = "UPDATE_INFO_MAIN_TEXT_VIEW";
    private final static String UPDATE_INFO_TRANSLATE = "UPDATE_INFO_TRANSLATE";
    private final static String UPDATE_INFO_COUNT = "UPDATE_INFO_COUNT";
    private final static String UPDATE_INFO_WORD_COUNT = "UPDATE_INFO_WORD_COUNT";

    public static void saveUpdateInfo(UpdateInfo updateInfo) {
        Context context = Application.getContext();
        SharedPreferences prefs = context.getSharedPreferences(UPDATE_INFO_PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UPDATE_INFO_MAIN_TEXT_VIEW, updateInfo.mainTextView);
        editor.putString(UPDATE_INFO_TRANSLATE, updateInfo.translateText);
        editor.putInt(UPDATE_INFO_COUNT, updateInfo.count);
        editor.putInt(UPDATE_INFO_WORD_COUNT, updateInfo.wordCount);
        editor.commit();
    }

    public static UpdateInfo getUpdateInfo(Context context) {
        MainDbForUser mainDbForUser = new MainDbForUser(context);
        MainDb mainDb = new MainDb(context);
        SharedPreferences prefs = context.getSharedPreferences(UPDATE_INFO_PREFS, 0);
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.mainTextView = prefs.getString(UPDATE_INFO_MAIN_TEXT_VIEW,
                mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(0)));
        updateInfo.translateText = prefs.getString(UPDATE_INFO_TRANSLATE,
                mainDb.getTranslateById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(0)));
        updateInfo.count = prefs.getInt(UPDATE_INFO_COUNT, 1);
        updateInfo.wordCount = prefs.getInt(UPDATE_INFO_WORD_COUNT, 0);
        return updateInfo;
    }

}
