package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.db.UserDb;
import com.almexe.lingvaproject.utils.CircleTransform;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StartFragment extends AbstractFragment{

    public static final String KEY = "Key";
    private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    Utils utils;
    protected Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils = new Utils();

        /*dialog creating only one */
        if(utils.getPreference(getActivity(), KEY)){
            showDialog();
            utils.setPreference(getActivity(), false, KEY);
        }

        try {
            new GetDataFromDb().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_start, container, false);

        /*Если залогированы то повляется веб-форма VK или же создаем/берем дефолтного юзера*/
        if(VKSdk.isLoggedIn())
            VKSdk.login(StartFragment.this, scope);
        else{
            try {
                new VkErrorResponse().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        /*Login/Logout Button*/
        Vkloginlogout();

        return v;
    }

    private StartFragment(){}

    public void setContext(Context context) {
        this.context = context;
    }

    public static StartFragment getInstance(Context context){
        Bundle args = new Bundle();
        StartFragment fragment = new StartFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        //fragment.setTitle(context.getString(R.string.history));
        return fragment;
    }

    void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.start_alert);

        TextView text = (TextView) dialog.findViewById(R.id.textStartDialog);
        Button button = (Button)dialog.findViewById(R.id.nothx);
        Button buttonYesVk = (Button)dialog.findViewById(R.id.yesVk);

        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);

        text.setTypeface(type2);
        button.setTypeface(type2);
        buttonYesVk.setTypeface(type2);

        buttonYesVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(StartFragment.this, scope);
                dialog.cancel();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new VkErrorResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });

        dialog.show();
    }
}