package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class CheckFragment extends Fragment implements OnClickListener{

    Button              btnCheck;
    public static TextView		    mainDataTextView, countWord;
    RadioButton                     chk1, chk2, chk3, chk4, checkedButton;
    String wordsFromTextView;
    RadioGroup radioGroup;
    String rightResult;
    String wordFromCheckedButton;
    int moveThoughtArray = 0;
    int count = 0;
    SpeechSynthesis synthesis;
    View layout;
    String mainWord;
    ArrayList<String> forList;
    LinearLayout activity;

    MainDbForUser mainDbForUser;
    ArrayList<String> list = new ArrayList<>();

    //public static ArrayList<String> rightResultWords = new ArrayList<>();
    public static ArrayList<String> wrongResultWords;
    private MainDb mainDb;
    private Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_check, container, false);

        prepareTTSEngine();

        synthesis.setStreamType(AudioManager.STREAM_MUSIC);

        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        wrongResultWords = new ArrayList<>();
        wrongResultWords.clear();
        utils = new Utils();

        mainDataTextView = (TextView)v.findViewById(R.id.mainDataTextView);
        countWord = (TextView)v.findViewById(R.id.countWord);

        btnCheck = (Button)v.findViewById(R.id.btnCheck);
        chk1 = 	   (RadioButton)v.findViewById(R.id.chk1);
        chk2 = 	   (RadioButton)v.findViewById(R.id.chk2);
        chk3 = 	   (RadioButton)v.findViewById(R.id.chk3);
        chk4 = 	   (RadioButton)v.findViewById(R.id.chk4);

        radioGroup = (RadioGroup)v.findViewById(R.id.radioGroup);
        ImageButton voiceButton = (ImageButton) v.findViewById(R.id.voice);
        layout = inflater.inflate(R.layout.toast, (ViewGroup) v.findViewById(R.id.toast_layout_root));
        activity = (LinearLayout)v.findViewById(R.id.LinearLayout);

        try {
            new ChangeWords().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        btnCheck.setOnClickListener(this);
        mainDataTextView.setOnClickListener(this);
        voiceButton.setOnClickListener(this);

        font();

        //Driver.imageViewVk.setVisibility(View.INVISIBLE);

        return v;
    }

    private void prepareTTSEngine() {
        try {
            synthesis = SpeechSynthesis.getInstance(getActivity());
        } catch (InvalidApiKeyException e1) {
            e1.printStackTrace();
        }
    }

    public void font() {
        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);
        mainDataTextView.setTypeface(mainFont);
        btnCheck.setTypeface(mainFont);
        chk1.setTypeface(mainFont);
        chk2.setTypeface(mainFont);
        chk3.setTypeface(mainFont);
        chk4.setTypeface(mainFont);
    }

	/*
	 * cutTheSentence read a speech in TextView
	 * and truncate the speech to one word
	 */

    private String cutTheSentence(String words) {
        ArrayList<String> charList = new ArrayList<>();
        char[] charArray = words.toCharArray();
        String result = null;
        for(char output : charArray) {
            if(output== ',') break;
            charList.add(String.valueOf(output));
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            for (String s : charList)
            {
                sb.append(s);
            }
            result = sb.toString();
        }
        return result;
    }

    public LinkedHashSet<Integer> randomNum(int maxNumber) {
        LinkedHashSet<Integer> list = new LinkedHashSet<>();
        Integer res;
        Random r = new Random();
        for(int i = 0; i < 1000; i++) {
            res = r.nextInt(maxNumber);
            list.add(res);
        }
        return list;
    }

    private class ChangeWords extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<Integer> number4 = new ArrayList<>(randomNum(10));
            Collections.shuffle(number4);

            //move thought a db from 1 to 10 / foreign words
            mainWord = mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(moveThoughtArray));

            ArrayList<String> arrayNativWords = new ArrayList<>();

            for(int i = 0; i < 10; i++){
                list.add(mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.TEN).get(i)));
            }

            //add arrayNativWords a cut sentence
            for(String word : list) {
                arrayNativWords.add(cutTheSentence(word));
            }

            forList = new ArrayList<>();
            rightResult = mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN).get(moveThoughtArray));
            forList.add(cutTheSentence(rightResult));

            int i = 0;

            while(forList.size() != 4){

                if (!arrayNativWords.get(number4.get(i)).trim().equals(cutTheSentence(rightResult).trim())) {
                    forList.add(arrayNativWords.get(number4.get(i)));
                }else{
                    arrayNativWords.get(i++);
                }
                i++;
            }

            Log.i("forList", String.valueOf(forList));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mainDataTextView.setText(mainWord);
            Collections.shuffle(forList);
            try{
                chk1.setText(forList.get(0));
                chk2.setText(forList.get(1));
                chk3.setText(forList.get(2));
                chk4.setText(forList.get(3));
                Log.i("forList = ", String.valueOf(forList.size()));
            }catch(Exception e) {
                e.printStackTrace();
            }

            if(moveThoughtArray != mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.TEN)-1) moveThoughtArray++;
        }
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.voice:

                try {
                    synthesis.speak(mainDataTextView.getText().toString());
                } catch (BusyException | NoNetworkException e1) {
                    e1.printStackTrace();
                }

                break;

            case R.id.btnCheck:
                try{
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    checkedButton = (RadioButton)getActivity().findViewById(selectedId);
                    wordFromCheckedButton = checkedButton.getText().toString();
                    wordsFromTextView = mainDataTextView.getText().toString();

                    chk1.setText(null);
                    chk2.setText(null);
                    chk3.setText(null);
                    chk4.setText(null);

                    if(wordFromCheckedButton.equals(cutTheSentence(rightResult))) {

                        //rightResultWords.add(wordsFromTextView);

                    }else{
                        wrongResultWords.add(wordsFromTextView);
                    }

                    try {
                        new ChangeWords().execute().get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(count == 9) {
                        if(wrongResultWords.size() == 0){
                            //utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
                            utils.transactions(getFragmentManager(), new LessonTenWordFragment(), Utils.LESSON_TEN_WORDS_FRAGMENT);

                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.nowrong_alert
                            );
                            TextView text = (TextView) dialog.findViewById(R.id.textCustomDialog);
                            Button okButton = (Button) dialog.findViewById(R.id.okButton);

                            Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);

                            text.setTypeface(type2);
                            okButton.setTypeface(type2);
                            okButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                }
                            });

                            dialog.show();


                        }else{
                            Log.e("wrongResultWords", String.valueOf(wrongResultWords));
                            //utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
                            utils.transactions(getFragmentManager(), new WrongResultCheckFragment(), Utils.WRONG_RESULT_FRAGMENT);
                        }

                    }
                    count++;

                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}