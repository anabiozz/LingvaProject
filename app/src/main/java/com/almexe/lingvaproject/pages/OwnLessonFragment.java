package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
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

public class OwnLessonFragment extends Fragment implements OnClickListener{

	ImageButton						deleteWord, voice, menu;
	public static TextView		    mainDataTextView, countWord, tranlate;
	private static final String 	KEY_INDEX = "index";
	String wordsFromTextView = null;
	SpeechSynthesis  synthesis;
	int count = 1;
	Utils utils;
	MainDb mainDb;
	static int wordCount = 0;

	MainDbForUser mainDbForUser;

	private VKApiUser user;
	private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
	private UserDb userDb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prepareTTSEngine();
		synthesis.setStreamType(AudioManager.STREAM_MUSIC);
		mainDbForUser = new MainDbForUser(getActivity());
		utils = new Utils();
		mainDb = new MainDb(getActivity());
		userDb = new UserDb(getActivity());
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

		if(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) == 0){
			deleteWord.setEnabled(false);
		}else{

			mainDataTextView.setText(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),
					MainDbForUser.OWN).get(wordCount)));
			tranlate.setText(mainDb.getTranslateById(mainDbForUser.getListId(Tables.getTableMain(),
					MainDbForUser.OWN).get(wordCount)));
			countWord.setText(count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));

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

		Driver.imageViewVk.setVisibility(View.VISIBLE);

		Driver.imageViewVk.setOnClickListener(new View.OnClickListener() {
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
					VKSdk.login(OwnLessonFragment.this, scope);
				}
			}
		});

		return v;
	}

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

						if (mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

							for (int i = 0; i < 10; i++) {

								int result = mainDbForUser.getNumber(i, Tables.getTableMain());

								mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
							}
						}
						Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
					}else {

						Tables.setTableMain("user" + "_" + user.id);
						Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
					}
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			//linlaHeaderProgress.setVisibility(View.GONE);
		}
	}

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

				if (mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

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
			Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));

			//linlaHeaderProgress.setVisibility(View.GONE);
		}
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
					if(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) != 0){
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
						if(wordCount != mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN)){
							mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(++count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));

						}else{
							wordCount = 0;
							count = 0;
							mainDataTextView.setText(mainDb.readNextForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(++count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));
						}
					}
					// Swipe right (previous)
					if (e1.getX() < e2.getX()) {
						--wordCount;
						if(wordCount != -1){
							mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(--count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));
						}else{
							wordCount = mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) - 1;
							count = mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) + 1;
							mainDataTextView.setText(mainDb.readPreviousForegin(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							tranlate.setText(mainDb.readNowTrans(mainDbForUser.getListId(Tables.getTableMain(),
									MainDbForUser.OWN).get(wordCount)));
							countWord.setText(--count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));
						}
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
					countWord.setText(--count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));
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
					countWord.setText(count + "/" + mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN));
				}

				if(mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView)
						&& mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) == 1){

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
					utils.transactions(getFragmentManager(), new LessonTenWordFragment());

				}else if(mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView)
						&& mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) == 1){

					if(mainDb.isRowExists(wordsFromTextView))
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
					else
						mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));

					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
					utils.transactions(getFragmentManager(), new LessonTenWordFragment());
				}

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

	private void deleteFromList() {
		mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.OWN)
                .remove(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(wordCount));
	}

	private void updateToZiro(){
		if(mainDb.getForeinWordById(mainDbForUser.getListId(Tables.getTableMain(),MainDbForUser.OWN).get(0)).equals(wordsFromTextView))
			mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(wordsFromTextView));
		else
			mainDbForUser.updateToNull(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdNativeWord(wordsFromTextView));
	}
}