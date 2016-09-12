package com.almexe.lingvaproject.pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.ExamplesDb;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.ParseUrl;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

public class OwnLessonFragment extends BaseFragment implements OnClickListener{

	protected ImageButton deleteWord, voice, menu;
    protected static TextView mainDataTextView, countWord, tranlate;
    protected static final String 	KEY_INDEX = "index";
    protected SpeechSynthesis  synthesis;
    protected int count = 1;
    protected Utils utils;
    protected MainDb mainDb;
    protected int wordCount = 0;
    protected ExamplesDb examplesDb;

	MainDbForUser mainDbForUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prepareTTSEngine();
		synthesis.setStreamType(AudioManager.STREAM_MUSIC);
		mainDbForUser = new MainDbForUser(getActivity());
		utils = new Utils();
		mainDb = new MainDb(getActivity());
        examplesDb = new ExamplesDb(getActivity());
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_own_lesson, container, false);

		mainDataTextView = (TextView)v.findViewById(R.id.mainOwnDataTextView);
		tranlate =         (TextView)v.findViewById(R.id.translat);
		countWord =         (TextView)v.findViewById(R.id.countWord);
		deleteWord = (ImageButton)v.findViewById(R.id.dontLearnWord);
		voice = (ImageButton)v.findViewById(R.id.voice);
		menu = (ImageButton)v.findViewById(R.id.exercise);

		font();

		wordCount = 0;

		if(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) == 0){
			deleteWord.setEnabled(false);
		}else{

			mainDataTextView.setText(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),
					MainDbForUser.OWN).get(wordCount)));
			tranlate.setText(mainDb.getTranslateById(mainDbForUser.getListId(Tables.getTableMain(),
					MainDbForUser.OWN).get(wordCount)));
			countWord.setText(count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));

			mainDataTextView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gesture.onTouchEvent(event);
				}
			});
		}

		menu.setOnClickListener(this);
		deleteWord.setOnClickListener(this);
		voice.setOnClickListener(this);
		mainDataTextView.setOnClickListener(this);

        parseExamples();
		return v;
	}

	private void prepareTTSEngine() {
		try {
			synthesis = SpeechSynthesis.getInstance(getActivity());
		} catch (InvalidApiKeyException e1) {
			e1.printStackTrace();
		}
	}

	final GestureDetector gesture = new GestureDetector(getActivity(),
			new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onDown(MotionEvent e) {
					return true;
				}

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					if(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) != 0){
						if(mainDataTextView.getText().equals(mainDb.readNowNative(mainDbForUser.getListId(Tables.getTableMain(),
								MainDbForUser.OWN).get(wordCount)))){

							mainDataTextView.setText(mainDb.readNowForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
						}else{
							mainDataTextView.setText(mainDb.readNowNative(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
						}
					}
					return super.onSingleTapConfirmed(e);
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
									   float velocityY) {

					if (e1.getX() > e2.getX()) {
						++wordCount;
						if(wordCount != mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN)){
							mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(++count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));

						}else{
							wordCount = 0;
							count = 0;
							mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(++count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));
						}
                        parseExamples();
					}
					// Swipe right (previous)
					if (e1.getX() < e2.getX()) {
						--wordCount;
						if(wordCount != -1){
							mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(--count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));
						}else{
							wordCount = mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) - 1;
							count = mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) + 1;
							mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(--count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));
						}
                        parseExamples();
					}
					return true;
				}
			});

	public void font() {
		Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);
		mainDataTextView.setTypeface(type2);
		tranlate.setTypeface(type2);
		countWord.setTypeface(type2);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		float fSize = Float.parseFloat(prefs.getString(
				getString(R.string.pref_size), "30"));

		mainDataTextView.setTextSize(fSize);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(KEY_INDEX, count);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.dontLearnWord:

				String wordsFromTextView = mainDataTextView.getText().toString();

				if(!(wordsFromTextView.equals(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.OWN).get(0))))
						&& mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).size() > 1
						&& !(wordsFromTextView.equals(mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.OWN).get(0))))){

					deleteFromList();

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					--wordCount;
					mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
							MainDbForUser.OWN).get(wordCount)));
					tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
							MainDbForUser.OWN).get(wordCount)));
					countWord.setText(--count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));
				}

				if((mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView))
						&& mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).size() > 1
						|| mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView)
						&& mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).size() > 1){

					deleteFromList();

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					mainDataTextView.setText(mainDb.readNowForegin(mainDbForUser.getListId(Tables.getTableMain(),
							MainDbForUser.OWN).get(wordCount)));
					tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
							MainDbForUser.OWN).get(wordCount)));
					countWord.setText(count + "/" + mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN));
				}

				if(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView)
						&& mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) == 1){

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
					utils.transactionsWithAnimation(getFragmentManager(), new LessonTenWordFragment(), Utils.LESSON_TEN_WORDS_FRAGMENT);

				}else if(mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView)
						&& mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) == 1){

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
					utils.transactionsWithAnimation(getFragmentManager(), new LessonTenWordFragment(), Utils.LESSON_TEN_WORDS_FRAGMENT);
				}

				break;

			case R.id.voice:
				try {
					synthesis.speak(mainDataTextView.getText().toString());
				} catch (BusyException | NoNetworkException e1) {
					e1.printStackTrace();
				}
				break;

			case R.id.exercise:
                setPreference(getActivity(), mainDataTextView.getText().toString(), Utils.BUNDLE);
                utils.transactionsWithAnimation(getFragmentManager(), new SentencesExamplesFragment(), Utils.EXAMPLES_SENTENCES_FRAGMENT);
				break;
		}
	}

    public void setPreference(Context c, String value, String key) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Utils.MyPREFERENCES, 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Utils.BUNDLE, mainDataTextView.getText().toString());
        editor.apply();
    }

	private void deleteFromList() {
		mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.OWN)
                .remove(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(wordCount));
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
}