package com.almexe.lingvaproject.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.utils.Utils;
import com.almexe.lingvaproject.utils.VKRegistration;
import com.vk.sdk.VKScope;

public class StartFragment extends AppCompatActivity {

    protected String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    protected Utils utils;
    protected Context context;
    protected VKRegistration registration;
    protected static StartFragment sStartFragment = null;
    private ProgressBar progressBar;

    public StartFragment(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start);
        progressBar = (ProgressBar)findViewById(R.id.marker_progress);
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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

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