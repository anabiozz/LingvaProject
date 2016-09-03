package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.almexe.lingvaproject.Application;
import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.ExamplesDb;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.ParseUrl;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.UpdateInfo;
import com.almexe.lingvaproject.utils.Utils;

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LessonTenWordFragment extends Fragment implements OnClickListener {

    protected ImageButton  examples;
    public TextView mainDataTextView;
    public TextView translate;
    protected TextView countWord;
    protected SpeechSynthesis synthesis;
    protected  String wordFromTextView;
    protected long lastCheckedMillis;
    protected long now;
    public int count = 1;
    protected Utils utils;
    protected MainDbForUser mainDbForUser;
    protected MainDb mainDb;
    public int wordCount = 0;
    FragmentManager fragmentManager;

    public String mainTextView;
    public String translateText;

    public static final String TAG = "LessonTenWordFragment";

    protected ExamplesDb examplesDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        utils = new Utils();
        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        /***************************************************/
        /*method change lesson words*/
        toChangeWords();
        /***************************************************/
        /*music*/
        prepareTTSEngine();
        synthesis.setStreamType(AudioManager.STREAM_MUSIC);
        /***************************************************/
        fragmentManager = getFragmentManager();

        examplesDb = new ExamplesDb(getActivity());
    }

    /*******************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lesson_ten_word, container, false);
        Resources res = getResources();
        mainDataTextView = (TextView)v.findViewById(R.id.mainOwnDataTextView);

        translate =         (TextView)v.findViewById(R.id.translat);
        countWord =         (TextView)v.findViewById(R.id.countWord);
        ImageButton exerciseButton = (ImageButton) v.findViewById(R.id.exercise);
        ImageButton voiceButton = (ImageButton) v.findViewById(R.id.voice);
        examples = (ImageButton)v.findViewById(R.id.examples);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        exerciseButton.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        mainDataTextView.setOnClickListener(this);
        examples.setOnClickListener(this);
        fab.setOnClickListener(this);
        Log.e(TAG, "onCreateView");

        UpdateInfo updateInfo = Application.getUpdateInfo(getActivity());
        mainTextView = updateInfo.mainTextView;
        translateText = updateInfo.translateText;
        count = updateInfo.count;
        wordCount = updateInfo.wordCount;

        mainDataTextView.setText(mainTextView);
        translate.setText(translateText);

        String resultCountWord = String.format(res.getString(R.string.result_count_word), count, 10);
        countWord.setText(resultCountWord);

        Log.e(TAG, String.valueOf(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN)));
        font();
        parseExamples();
        Log.e(TAG, String.valueOf(examplesDb.fetchPlacesCount(mainDataTextView.getText().toString())));

        mainDataTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        return v;
    }

    public ArrayList<String> findWord(String string) {
        List<String> tokens = new ArrayList<>();
        ArrayList<String> resultString = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(string);
        //("---- Split by space ------");
        while (st.hasMoreElements()) {
            tokens.add(st.nextElement().toString());
            tokens.add(" ");
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i < tokens.size(); i++) {
            if(tokens.get(i).contains(mainDataTextView.getText().toString().trim())) {
                resultString.add(builder.toString());
                resultString.add(tokens.get(i));
                i++;
                builder.setLength(0);
                while (i < tokens.size()) {
                    builder.append(tokens.get(i));
                    i++;
                }
                resultString.add(builder.toString());
                break;
            }
            builder.append(tokens.get(i));
        }
        return resultString;
    }

    public void parseExamples() {
        if(!examplesDb.isRowExists(mainDataTextView.getText().toString()))
            new ParseUrl().execute(mainDataTextView.getText().toString());
        /*else {
            String one = findWord(examplesDb.getWord(mainDataTextView.getText().toString())).get(0);
            String two = "<font color='#ffffff'>"+findWord(examplesDb.getWord(mainDataTextView.getText().toString())).get(1)+"</font>";
            String three = findWord(examplesDb.getWord(mainDataTextView.getText().toString())).get(2);
        }*/
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        LessonTenWordFragment lessonTenWordFragment = (LessonTenWordFragment)
                getFragmentManager().findFragmentByTag(Utils.LESSON_TEN_WORDS_FRAGMENT);
        Application.saveUpdateInfo(new UpdateInfo(lessonTenWordFragment));
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        float fSize = Float.parseFloat(prefs.getString(
                getString(R.string.pref_size), "30"));
        if(fSize > 30) fSize = 30;
        mainDataTextView.setTextSize(fSize);
    }

    /*private String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }

    private String removeNotEnglish(String commentstr)
    {
        String urlPattern = "!/^[\\x20-\\x7E]+$/";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }

    private String removeDog(String commentstr)
    {
        String urlPattern = "!/^[A-Z]+$/i";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }*/

    /*public void getTweet() {
        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query(mainDataTextView.getText().toString())
                .build();

        searchTimeline.next(510908133917487104L, new Callback<TimelineResult<Tweet>>() {

            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                if (!result.data.items.isEmpty()) {
                    String rowString = result.data.items.get(0).text;
                    //twitts.setText(removeDog(removeNotEnglish(removeUrl(rowString))));
                } else {
                    Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    /*******************************************************************************************/
    private void toChangeWords() {
        now = System.currentTimeMillis();
        lastCheckedMillis = getPreference(getActivity(), Tables.getTableMain());
        Log.e(TAG, String.valueOf(lastCheckedMillis) + " lastCheckedMillis");
        if(lastCheckedMillis == 0){
            lastCheckedMillis =  System.currentTimeMillis();
            setPreference(getActivity(), lastCheckedMillis, Tables.getTableMain());
        }else{
            long diffMillis = now - lastCheckedMillis;
            if( diffMillis >= (Constants.WORD_CHANGE_TIME) ) {
                lastCheckedMillis =  System.currentTimeMillis();
                setPreference(getActivity(), lastCheckedMillis, Tables.getTableMain());
                showDialog();
            }
        }
    }

    /****************************************************************************************/
    public void setPreference(Context c, long value, String key) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Utils.MyPREFERENCES, 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setPreference(Context c, String value, String key) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Utils.MyPREFERENCES, 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Utils.BUNDLE, mainDataTextView.getText().toString());
        editor.apply();
    }

    public long getPreference(Context c, String key) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Utils.MyPREFERENCES, 0);
        return sharedpreferences.getLong(key, 0);
    }
    /*************************************************************************************/
    private void prepareTTSEngine() {
        try {
            synthesis = SpeechSynthesis.getInstance(getActivity());
        } catch (InvalidApiKeyException e1) {
            e1.printStackTrace();
        }
    }
    /*******************************************************************************************/
    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN) != 0){
                        if(mainDataTextView.getText().equals(mainDb.readNowNative(mainDbForUser.getListId(Tables.getTableMain(),
                                MainDbForUser.TEN).get(wordCount)))){

                            mainDataTextView.setText(mainDb.readNowForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                        }else{
                            mainDataTextView.setText(mainDb.readNowNative(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                        }
                    }
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    if (e1.getX() > e2.getX()) {
                        wordCount++;
                        if(wordCount != mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN)){
                            mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            String resultCountWord = String.format("%d/%d", ++count, mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN));
                            countWord.setText(resultCountWord);
                        }else{
                            wordCount = 0;
                            count = 0;
                            mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(++count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN));
                        }
                        parseExamples();
                    }

                    // Swipe right (previous)
                    if (e1.getX() < e2.getX()) {
                        --wordCount;
                        if(wordCount != -1){
                            mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(--count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN));
                        }else{
                            wordCount = mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).size()-1; //9
                            count = mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).size()+1;     //11
                            mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(--count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN));
                        }
                        parseExamples();
                    }
                    return true;
                }
            });
    /***********************************************************************************/
    private void font() {
        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);
        //exerciseButton.setTypeface(mainFont);
        translate.setTypeface(mainFont);
        //voiceButton.setTypeface(mainFont);
        mainDataTextView.setTypeface(mainFont);
        countWord.setTypeface(mainFont);
    }
    /***************************************************************************************/

    class UpdateLearn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i = 0; i < 10; i++) {
                mainDbForUser.update(Tables.getTableMain(), MainDbForUser.LEARNED, mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ChangeWords().execute();
        }
    }

    class ChangeWords extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.TEN);
            while (mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN) < 10) {
                for (int i = 0; i < 10; i++) {
                    int result = mainDbForUser.getNotLearnedWords(i, Tables.getTableMain());
                    mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, String.valueOf(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED)));
            Fragment frg = getFragmentManager().findFragmentByTag(Utils.LESSON_TEN_WORDS_FRAGMENT);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
            Driver driver = (Driver) getActivity();
            Utils.updateText(driver, String.valueOf(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED)), R.id.numberlLearnedWords);
        }
    }

    /*******************************************************************************************/
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.exercise:
                utils.transactionsWithAnimation(getFragmentManager(), new CheckFragment(), Utils.CHECK_FRAGMENT);
                break;

            case R.id.fab:

                wordFromTextView = mainDataTextView.getText().toString();

                if(wordFromTextView.equals(mainDb.getNativeWord(wordFromTextView)))
                    mainDbForUser.update(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordFromTextView));
                else
                    mainDbForUser.update(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordFromTextView));

                utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
                        "ДОБАВЛЕНО В СЛОВАРЬ", Toast.LENGTH_SHORT);

                break;

            case R.id.voice:

                try {
                    synthesis.speak(mainDataTextView.getText().toString());
                } catch (BusyException | NoNetworkException e1) {
                    e1.printStackTrace();
                }
                break;

            case R.id.examples:
                setPreference(getActivity(), mainDataTextView.getText().toString(), Utils.BUNDLE);
                utils.transactionsWithAnimation(getFragmentManager(), new SentencesExamplesFragment(), Utils.EXAMPLES_SENTENCES_FRAGMENT);
                break;
        }
    }
    /******************************************************************************************/
    void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert);

        TextView text = (TextView) dialog.findViewById(R.id.textCustomDialog);
        Button learnBtn = (Button) dialog.findViewById(R.id.learnDialogBtn);
        Button dontLearnBtn = (Button) dialog.findViewById(R.id.dontLearnDialogBtn);

        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);

        text.setTypeface(type2);
        learnBtn.setTypeface(type2);
        learnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                /*if(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED) == 10){
                    utils.wallPost(getActivity(), "Ура вы изучили 10 слов");
                }*/

                new UpdateLearn().execute();

                dialog.cancel();
            }
        });
        dontLearnBtn.setTypeface(type2);
        dontLearnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}