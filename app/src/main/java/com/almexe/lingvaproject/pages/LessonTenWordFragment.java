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

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.CallBack;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LessonTenWordFragment extends Fragment implements OnClickListener {

    protected ImageButton  examples;
    protected TextView mainDataTextView;
    protected TextView translate;
    protected TextView countWord;
    protected SpeechSynthesis synthesis;
    protected  String wordFromTextView;
    protected SharedPreferences sharedpreferences;
    protected long lastCheckedMillis;
    protected long now;
    protected int count = 1;
    protected Utils utils;
    protected MainDbForUser mainDbForUser;
    protected MainDb mainDb;
    protected int wordCount = 0;
    FragmentManager fragmentManager;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String TAG = "LessonTenWordFragment";
    private TextView twitts;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /*Login/Logout Button*/
        //Vkloginlogout();
    }

    /*******************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lesson_ten_word, container, false);
        Resources res = getResources();
        //tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimaryDark));
        mainDataTextView = (TextView)v.findViewById(R.id.mainOwnDataTextView);
        twitts = (TextView) v.findViewById(R.id.twitts);

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
        wordCount = 0;
        mainDataTextView.setText(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(0)));
        translate.setText(mainDb.getTranslateById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(0)));
        String resultCountWord = String.format(res.getString(R.string.result_count_word), count,
                mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN));
        countWord.setText(resultCountWord);

        Log.e(TAG, String.valueOf(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN)));
        font();

        getTweet();

        mainDataTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });


        return v;
    }
    private String removeUrl(String commentstr)
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
    }




    public void getTweet() {
        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query(mainDataTextView.getText().toString())
                .build();

        searchTimeline.next(510908133917487104L, new Callback<TimelineResult<Tweet>>() {

            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                if (!result.data.items.isEmpty()) {
                    String rowString = result.data.items.get(0).text;
                    twitts.setText(removeDog(removeNotEnglish(removeUrl(rowString))));
                } else {
                    Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*******************************************************************************************/
    private void toChangeWords(){
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
    /**************************************************************************************/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("count", count);
        savedInstanceState.putString("maintext", mainDbForUser.readNowForegin(Tables.getTableMain()));
        savedInstanceState.putString("trans", mainDbForUser.readNowTrans(Tables.getTableMain()));
    }
    /****************************************************************************************/
    public void setPreference(Context c, long value, String key) {
        sharedpreferences = c.getSharedPreferences(MyPREFERENCES, 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getPreference(Context c, String key) {
        sharedpreferences = c.getSharedPreferences(MyPREFERENCES, 0);
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
                        ++wordCount;
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
                        getTweet();
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
                        getTweet();
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

    public void register(CallBack callBack) {
        mainDbForUser = new MainDbForUser(getActivity());
        callBack.setNumberLearnedWords(String.valueOf(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED)));
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
                fragmentManager.beginTransaction().replace(R.id.content_frame, new CheckFragment()).commit();

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
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SentencesExamplesFragment()).commit();

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