package com.almexe.lingvaproject.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.db.ExamplesDb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class ParseUrl extends AsyncTask<String, Void, String> {

    private static final String TAG = "ParseUrl";
    URL url;
    InputStream is = null;
    BufferedReader br;
    String line;
    ExamplesDb examplesDb;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        examplesDb = new ExamplesDb(Application.getContext());
    }

    @Override
    protected String doInBackground(String... string) {
        StringBuilder buffer = new StringBuilder();
        try {
            url  = new URL("http://sentence.yourdictionary.com/"+ string[0]);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                Log.e(TAG, line);
            }
            Document doc = Jsoup.parse(sb.toString());
            Elements els = doc.select("div.li_content");
            for (Element element : els) {
                /*buffer.append(element.text());
                buffer.append(System.lineSeparator());*/
                examplesDb.write(element.text(), string[0]);
            }
        } catch (Throwable t) {t.printStackTrace();}
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}