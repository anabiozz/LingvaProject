package com.almexe.lingvaproject.db;

import android.os.AsyncTask;
import com.almexe.lingvaproject.Application;
import java.io.IOException;

public class GetDataFromDb extends AsyncTask<Void, Void, Void>{

    public GetDataFromDb() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDb mainDb = new MainDb(Application.getContext());
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
    }
}
