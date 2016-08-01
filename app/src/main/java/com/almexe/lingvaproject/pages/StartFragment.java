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

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.InitialService;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;
import com.almexe.lingvaproject.utils.VKRegistration;
import com.vk.sdk.VKScope;

public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";
    protected Utils utils;
    protected Context context;

    public StartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_start, container, false);
       /* MainDbForUser mainDbForUser = new MainDbForUser(Application.getContext());
        Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED)));*/
        Log.e(TAG, "onCreate");
        return v;
    }
}