package com.almexe.lingvaproject.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;

public class VkErrorResponse extends AsyncTask<Void, Void, Void> {
    private MainDbForUser mainDbForUser;
    private MainDb mainDb;

    public VkErrorResponse() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainDbForUser = new MainDbForUser(Application.getContext());
        mainDb = new MainDb(Application.getContext());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Tables.setTableMain("defaultuser");
            if(!mainDbForUser.isExists(Tables.getTableMain())) {
                mainDbForUser.createTable(Tables.getTableMain());
                mainDbForUser.insert(Tables.getTableMain());
                while (mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN) != 10) {
                    for (int i = 0; i < 10; i++) {
                        int result = mainDbForUser.getNotLearnedWords(i, Tables.getTableMain());
                        mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeignWord(mainDb.getWord(result, 2)));
                    }
                }
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
    }
}
