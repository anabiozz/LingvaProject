package com.almexe.lingvaproject.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.pages.StartFragment;
import com.vk.sdk.VKSdk;

import java.util.concurrent.ExecutionException;

public class Receiver extends BroadcastReceiver {
    public Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Constants.ACTION_LOGIN_VK:
                if(VKSdk.isLoggedIn()){
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.logaut_alert);

                    TextView text = (TextView) dialog.findViewById(R.id.textAlertDialog);
                    Button logoutBtn = (Button) dialog.findViewById(R.id.logoutButton);
                    Button dontlogoutBtn = (Button) dialog.findViewById(R.id.dontLogout);

                    Typeface type2 = Typeface.createFromAsset(context.getAssets(), Constants.TYPEFONT);

                    text.setTypeface(type2);
                    logoutBtn.setTypeface(type2);
                    dontlogoutBtn.setTypeface(type2);

                    logoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VKSdk.logout();
                            /*try {
                                new VkErrorResponse().execute().get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }*/
                            dialog.cancel();
                        }
                    });

                    dontlogoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }else{
                    Intent vk_intent = new Intent(Application.getContext(), VKRegistration.class);
                    vk_intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Application.getContext().startActivity(vk_intent);
                    //VKRegistration.getInstance().login();
                }
                break;
        }
    }
}
