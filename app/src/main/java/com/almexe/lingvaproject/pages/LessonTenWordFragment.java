package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.almexe.lingvaproject.db.DbTenWords;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.db.UserDb;
import com.almexe.lingvaproject.utils.CircleTransform;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;
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

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

import java.util.concurrent.ExecutionException;

public class LessonTenWordFragment extends Fragment implements OnClickListener {

    ImageButton  addForLearning;
    public static TextView mainDataTextView;
    private TextView translate;
    private TextView countWord;
    SpeechSynthesis    		synthesis;
    String 					wordFromTextView;
    SharedPreferences sharedpreferences;
    long lastCheckedMillis;
    long now;
    public static final String MyPREFERENCES = "MyPrefs" ;
    int count = 1;

    private Utils utils;

    private VKApiUser user;

    private MainDbForUser mainDbForUser;
    MainDb mainDb;
    static int wordCount = 0;

    private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    private UserDb userDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils = new Utils();

        /*init db`s*/
        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        userDb = new UserDb(getActivity());
        /***************************************************/
        /*method change lesson words*/
        toChangeWords();
        /***************************************************/
        /*music*/
        prepareTTSEngine();
        synthesis.setStreamType(AudioManager.STREAM_MUSIC);
        /***************************************************/
        /*Login/Logout Button*/
        VkLogoutLogin();
    }

    /*******************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lesson_ten_word, container, false);

        //tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimaryDark));

        mainDataTextView = (TextView)v.findViewById(R.id.mainOwnDataTextView);
        translate =         (TextView)v.findViewById(R.id.translat);
        countWord =         (TextView)v.findViewById(R.id.countWord);
        ImageButton exerciseButton = (ImageButton) v.findViewById(R.id.exercise);
        ImageButton voiceButton = (ImageButton) v.findViewById(R.id.voice);
        addForLearning = (ImageButton)v.findViewById(R.id.addForLearning);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

        exerciseButton.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        mainDataTextView.setOnClickListener(this);
        addForLearning.setOnClickListener(this);
        fab.setOnClickListener(this);

        Log.e("onCreateView", "onCreateView");

        wordCount = 0;

        mainDataTextView.setText(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),
                MainDbForUser.TEN).get(0)));
        translate.setText(mainDb.getTranslateById(mainDbForUser.getListId(Tables.getTableMain(),
                MainDbForUser.TEN).get(0)));
        countWord.setText(count + "/" + mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN));

        Log.e("getListId", String.valueOf(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.TEN)));

        font();

        mainDataTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return v;
    }

    /*****************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(final VKAccessToken res) {

                try {
                    new VkResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VKError error) {

                try {
                    new VkErrorResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        })) {}
    }
    /*******************************************************************************************/
    private class VkResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    user = ((VKList<VKApiUser>) response.parsedModel).get(0);

                    Driver.headerName.setText(user.first_name);
                    Driver.headerLastName.setText(user.last_name);

                    Picasso.with(getActivity()).load(user.photo_200).
                            transform(new CircleTransform()).into(Driver.image);

                    Driver.image.setVisibility(View.VISIBLE);

                    if(!userDb.isRowExists(user.id)){
                        UserDb.user_id = user.id;
                        userDb.write();

                        Tables.setTableMain("user" + "_" + user.id);

                        mainDbForUser.createTable(Tables.getTableMain());
                        mainDbForUser.insert(Tables.getTableMain());

                        if (mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

                            for (int i = 0; i < 10; i++) {

                                int result = mainDbForUser.getNumber(i, Tables.getTableMain());

                                mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                            }
                        }
                        Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
                    }else {

                        Tables.setTableMain("user" + "_" + user.id);
                        Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //linlaHeaderProgress.setVisibility(View.GONE);
            utils.transactions(getFragmentManager(), new LessonTenWordFragment());

        }
    }

    /*****************************************************************************************/
    private class VkErrorResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Tables.setTableMain("defaultuser");

            if(!mainDbForUser.isExists(Tables.getTableMain())) {

                mainDbForUser.createTable(Tables.getTableMain());
                mainDbForUser.insert(Tables.getTableMain());

                if (mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

                    for (int i = 0; i < 10; i++) {

                        int result = mainDbForUser.getNumber(i, Tables.getTableMain());

                        mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.LEARNED)));

            //linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /*******************************************************************************************/
    private void toChangeWords(){

        now = System.currentTimeMillis();
        lastCheckedMillis = getPreference(getActivity(), Tables.getTableMain());

        if(lastCheckedMillis == 0){

            lastCheckedMillis =  System.currentTimeMillis();
            setPreference(getActivity(), lastCheckedMillis, Tables.getTableMain());
        }else{

            long diffMillis = now - lastCheckedMillis;

            if( diffMillis >= (Constants.WORD_CHANGE_TIME) ) {
                // store now (i.e. in shared prefs)

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
                    if(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN) != 0){
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
                        if(wordCount != mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN)){
                            mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(++count + "/" + mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN));

                        }else{
                            wordCount = 0;
                            count = 0;
                            mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(++count + "/" + mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN));
                        }
                    }

                    // Swipe right (previous)
                    if (e1.getX() < e2.getX()) {
                        --wordCount;
                        if(wordCount != -1){
                            mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(--count + "/" + mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN));
                        }else{
                            wordCount = mainDbForUser.getListId(Tables.getTableMain(), mainDbForUser.TEN).size()-1; //9
                            count = mainDbForUser.getListId(Tables.getTableMain(), mainDbForUser.TEN).size()+1;     //11
                            mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            translate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
                                    MainDbForUser.TEN).get(wordCount)));
                            countWord.setText(--count + "/" + mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN));
                        }

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

    /*******************************************************************************************/
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.exercise:

                FragmentManager fragmentManager = getFragmentManager();
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
        }
    }
    /*****************************************************************************************/
    private class GetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            for (int j = 0; j < 10; j++) {
                mainDbForUser.updateTo(Tables.getTableMain(), MainDbForUser.TEN);
            }


            if (mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

                for (int i = 0; i < 10; i++) {

                    int result = mainDbForUser.getNumber(i, Tables.getTableMain());

                    mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                    mainDbForUser.update(Tables.getTableMain(), MainDbForUser.LEARNED, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                }
            }
            return null;
        }

    }

    /******************************************************************************************/
    private void showDialog() {
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

                if(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.LEARNED) == 10){
                    utils.wallPost(getActivity(), "Ура вы изучили 10 слов");
                }

                try {
                    new GetData().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountTen(Tables.getTableMain(), MainDbForUser.LEARNED)));

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);

                fragmentTransaction.replace(R.id.content_frame, new LessonTenWordFragment()).commit();

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

    /******************************************************************************/
    private void VkLogoutLogin() {
        Driver.imageViewVk.setVisibility(View.VISIBLE);
        Driver.imageViewVk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VKSdk.isLoggedIn()){

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.logaut_alert);

                    TextView text = (TextView) dialog.findViewById(R.id.textAlertDialog);
                    Button logoutBtn = (Button) dialog.findViewById(R.id.logoutButton);
                    Button dontlogoutBtn = (Button) dialog.findViewById(R.id.dontLogout);

                    Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);

                    text.setTypeface(type2);
                    logoutBtn.setTypeface(type2);
                    dontlogoutBtn.setTypeface(type2);

                    logoutBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VKSdk.logout();
                            Driver.headerName.setText(null);
                            Driver.headerLastName.setText(null);
                            Driver.image.setVisibility(View.INVISIBLE);
                            try {
                                new VkErrorResponse().execute().get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            utils.transactions(getFragmentManager(), new LessonTenWordFragment());
                            dialog.cancel();
                        }
                    });

                    dontlogoutBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();

                }else{
                    VKSdk.login(LessonTenWordFragment.this, scope);
                }
            }
        });
    }

}