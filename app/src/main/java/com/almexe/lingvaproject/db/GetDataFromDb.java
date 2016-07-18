package com.almexe.lingvaproject.db;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;

import java.io.IOException;

public class GetDataFromDb extends AsyncTask<Void, Void, Void>{

    private LinearLayout linearLayout;
    Context context;

    public GetDataFromDb() {
        View v = View.inflate(Driver.getContext(), R.layout.activity_main, null);
        linearLayout = (LinearLayout) v.findViewById(R.id.header_progress);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDb mainDb = new MainDb(Driver.getContext());

        try {
            mainDb.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }try {
            mainDb.openDataBase();
        } catch (Exception ioe) {
            throw new Error("Unable to open database");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        linearLayout.setVisibility(View.INVISIBLE);
    }
}
