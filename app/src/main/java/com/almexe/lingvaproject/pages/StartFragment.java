package com.almexe.lingvaproject.pages;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.InitialService;
import com.almexe.lingvaproject.utils.Utils;
import com.almexe.lingvaproject.utils.VKRegistration;
import com.vk.sdk.VKScope;

public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";
    protected String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    protected Utils utils;
    protected Context context;
    protected VKRegistration registration;
    protected static StartFragment sStartFragment = null;

    public StartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_start, container, false);

        //	Vkloginlogout();

        Log.e(TAG, "onCreate");
        return v;
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_start, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.marker_progress);
        //utils = new Utils();
        //registration = new VKRegistration();
        //mainDbForUser = new MainDbForUser(getActivity());

        *//*dialog creating only one *//**//*
        if(utils.getPreference(getActivity(), KEY)){
            showDialog();
            utils.setPreference(getActivity(), false, KEY);
        }*//*

        *//*Login/Logout Button*//*
        //Vkloginlogout();

        return v;
    }*/

    /*void showDialog() {
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
    }*/
}