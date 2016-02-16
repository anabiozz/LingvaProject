package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.adapter.WordsListAdapter;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.db.UserDb;
import com.almexe.lingvaproject.utils.CircleTransform;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LearnedWordsFragment extends AbstractFragment{

    MainDbForUser mainDbForUser;
    MainDb mainDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_learned_words, container, false);

        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        userDb = new UserDb(getActivity());

        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new WordsListAdapter(createListData(),createNativeListData()));

        /*Login/Logout Button*/
        Vkloginlogout();

        return v;
    }

    private ArrayList<String> createListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).size(); i++){
           data.add( mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).get(i)));
        }
        return data;
    }

    private ArrayList<String> createNativeListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).size(); i++){
            data.add( mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).get(i)));
        }
        return data;
    }
}
