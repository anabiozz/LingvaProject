package com.almexe.lingvaproject.pages;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.adapter.SentencesListAdapter;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Tables;

import java.util.ArrayList;

public class SentencesExamplesFragment extends BaseFragment{

    MainDbForUser mainDbForUser;
    MainDb mainDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sentences_examples, container, false);
        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());

        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new SentencesListAdapter(createListData()));
        return v;
    }

    private ArrayList<String> createListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).size(); i++){
            data.add( mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).get(i)));
        }
        return data;
    }
}
