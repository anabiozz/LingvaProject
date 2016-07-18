package com.almexe.lingvaproject.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.concurrent.ExecutionException;

public class VKRegistration extends Activity{
    public String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    static VKRegistration sVKRegistration = null;

    public static VKRegistration getInstance() {
        if (sVKRegistration == null) {
            sVKRegistration = new VKRegistration();
        }
        return sVKRegistration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                try {
                    new VkResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                /*try {
                    new VkErrorResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }*/
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void login() {
        VKSdk.login(VKRegistration.this, scope);
    }
}
