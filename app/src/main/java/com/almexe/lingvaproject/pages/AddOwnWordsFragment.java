package com.almexe.lingvaproject.pages;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;

public class AddOwnWordsFragment extends BaseFragment implements OnClickListener{

	AutoCompleteTextView    foreignWordEditText;
	AutoCompleteTextView    nativWordEditText;
	Button      			addButton;
	Button  				proseedToLesson;
	TextInputLayout textInputLayout1, textInputLayout2;

	MainDb mainDb;

	MainDbForUser mainDbForUser;

	private Utils utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_add_own_words, container, false);

		mainDbForUser = new MainDbForUser(getActivity());
		mainDb = new MainDb(getActivity());
		utils = new Utils();
	//	userDb = new UserDb(getActivity());

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

	//	Vkloginlogout();

		return v;
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
				if(mainDbForUser.getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.OWN) != 0) {
					utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_3));
					utils.transactionsWithAnimation(getFragmentManager(), new OwnLessonFragment(), Utils.OWN_LESSON_FRAGMENT);

				}else{
					String PROSEED_NOK = "Слов не Добавлено";
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

					String TOAST_MESSAGE_OK = "Слова Добавлены";
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