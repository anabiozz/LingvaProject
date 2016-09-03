package com.almexe.lingvaproject.pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.adapter.SentencesListAdapter;
import com.almexe.lingvaproject.db.ExamplesDb;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Utils;

import java.util.ArrayList;

public class SentencesExamplesFragment extends BaseFragment{

    MainDbForUser mainDbForUser;
    MainDb mainDb;

    protected ExamplesDb examplesDb;

    public String getPreference(Context c, String key) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Utils.MyPREFERENCES, 0);
        return sharedpreferences.getString(Utils.BUNDLE, "");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sentences_examples, container, false);
        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        examplesDb = new ExamplesDb(getActivity());

        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new SentencesListAdapter(createListData()));

        if(getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener( new View.OnKeyListener() {
                @Override
                public boolean onKey( View v, int keyCode, KeyEvent event )
                {
                    if( keyCode == KeyEvent.KEYCODE_BACK )
                    {
                        getFragmentManager().popBackStack();
                        return true;
                    }
                    return false;
                }
            } );
        }
        return v;
    }



    private ArrayList<String> createListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < examplesDb.fetchPlacesCount(getPreference(getActivity(), Utils.BUNDLE)); i++){
            data.add(examplesDb.getWords(getPreference(getActivity(), Utils.BUNDLE), i));
        }
        return data;
    }
}
