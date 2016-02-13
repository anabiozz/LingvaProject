package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.concurrent.ExecutionException;

public class AddOwnWordsFragment extends Fragment implements OnClickListener{

	AutoCompleteTextView    foreignWordEditText;
	AutoCompleteTextView    nativWordEditText;
	Button      			addButton;
	Button  				proseedToLesson;
	TextInputLayout textInputLayout1, textInputLayout2;

	MainDb mainDb;

	MainDbForUser mainDbForUser;

	//private final String TOAST_MESSAGE_NO = "Вы Должны Написать Слово и Перевод";
	private final String TOAST_MESSAGE_OK = "Слова Добавлены";
	private final String PROSEED_NOK = "Слов не Добавлено";
	private Utils utils;

	private VKApiUser user;
	private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
	private UserDb userDb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_add_own_words, container, false);

		mainDbForUser = new MainDbForUser(getActivity());
		mainDb = new MainDb(getActivity());
		utils = new Utils();
		userDb = new UserDb(getActivity());

		mainDb.read();

		foreignWordEditText = (AutoCompleteTextView ) v.findViewById(R.id.foreign_word);
		nativWordEditText =   (AutoCompleteTextView ) v.findViewById(R.id.nativ_word);
		addButton = 		  (Button) v.findViewById(R.id.button_add);
		proseedToLesson =     (Button) v.findViewById(R.id.proseedToLesson);
		textInputLayout1 = (TextInputLayout)v.findViewById(R.id.foreign_word_layout);
		textInputLayout2 = (TextInputLayout)v.findViewById(R.id.nativ_layout);

		proseedToLesson.setOnClickListener(this);
		addButton.setOnClickListener(this);

		AutoCompleteTextView();

		font();

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
					VKSdk.login(AddOwnWordsFragment.this, scope);
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

	private void AutoCompleteTextView() {

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
				mainDb.notSortedListForForeignWords);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
				mainDb.notSortedListForNativWords);

		foreignWordEditText.setAdapter(adapter);
		nativWordEditText.setAdapter(adapter2);

		foreignWordEditText.setOnItemClickListener(

				new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
						String word = foreignWordEditText.getText().toString();
						String nativWord = mainDb.getWord(mainDb.getWord(word)-1, 3);
						nativWordEditText.setText(nativWord);
					}});

		nativWordEditText.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String word = nativWordEditText.getText().toString();
				String foreignWord = mainDb.getWord(mainDb.getWord(word)-1, 2);
				foreignWordEditText.setText(foreignWord);
			}});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.proseedToLesson:
				if(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.OWN) != 0) {
					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_3));
					utils.transactions(getFragmentManager(), new OwnLessonFragment());

				}else{
					utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
							PROSEED_NOK, Toast.LENGTH_SHORT);
				}

				break;

			case R.id.button_add:
				String textFW = foreignWordEditText.getText().toString();
				String textNW = nativWordEditText.getText().toString();

				if (textFW.equals("") && textNW.equals("")){
					utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
							getActivity().getString(R.string.TOAST_MESSAGE_NO), Toast.LENGTH_SHORT);

				}else if (textFW.equals("")){
					utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
							getActivity().getString(R.string.TOAST_MESSAGE_NO), Toast.LENGTH_SHORT);

				}else if (textNW.equals("")) {
					utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
							getActivity().getString(R.string.TOAST_MESSAGE_NO), Toast.LENGTH_SHORT);
				}else{

					mainDbForUser.update(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(textFW));

					foreignWordEditText.setText(null);
					nativWordEditText.setText(null);

					utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
							TOAST_MESSAGE_OK, Toast.LENGTH_SHORT);
				}
				break;
		}
	}

	private void font() {
		Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);
		textInputLayout1.setTypeface(type2);
		textInputLayout2.setTypeface(type2);
		foreignWordEditText.setTypeface(type2);
		nativWordEditText.setTypeface(type2);
		addButton.setTypeface(type2);
		proseedToLesson.setTypeface(type2);
	}

}