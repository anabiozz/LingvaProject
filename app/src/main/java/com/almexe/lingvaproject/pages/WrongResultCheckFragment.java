package com.almexe.lingvaproject.pages;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.adapter.RecyclerItemClickListener;
import com.almexe.lingvaproject.adapter.WordsListAdapter;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;

import java.util.ArrayList;

public class WrongResultCheckFragment extends Fragment implements View.OnClickListener{

    private TextView wrongResult;
    private TextView numberWrongResult;
    private Button resultButton;

    private ArrayList<String> list;
    private ArrayList<Integer> listId;

    private Utils utils;
    MainDbForUser mainDbForUser;
    private MainDb mainDb;
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_result_check, container,false);

        utils = new Utils();
        mainDb = new MainDb(getActivity());
        mainDbForUser = new MainDbForUser(getActivity());
        list = new ArrayList<>();
        listId = new ArrayList<>();

        wrongResult = (TextView) v.findViewById(R.id.wrongResult);
        numberWrongResult = (TextView) v.findViewById(R.id.numberWrongResult);
        resultButton = (Button) v.findViewById(R.id.resultbutton);

        rv = (RecyclerView)v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new WordsListAdapter(createListData(),createNativeListData()));

        String result2 = String.valueOf(CheckFragment.wrongResultWords.size());
        numberWrongResult.setText(result2);

        rv.addOnItemTouchListener(
            new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View view, int position) {
                    if(view.isSelected()){
                        try{
                            if(listId.size() != 0){
                                listId.remove(listId.indexOf(position));
                                listId.trimToSize();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        view.setSelected(false);
                    }
                    else{
                        listId.add(position);
                        view.setSelected(true);
                    }

                    Log.e("listId", String.valueOf(listId));

                }
            })
        );

        font();

        //Driver.getImageViewVk().setVisibility(View.INVISIBLE);

        resultButton.setOnClickListener(this);

        return v;
    }

    private void font() {
        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);
        numberWrongResult.setTypeface(mainFont);
        wrongResult.setTypeface(mainFont);
        resultButton.setTypeface(mainFont);
    }

    private ArrayList<String> createListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < CheckFragment.wrongResultWords.size(); i++){
            data.add(CheckFragment.wrongResultWords.get(i));
        }
        return data;
    }

    private ArrayList<String> createNativeListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < CheckFragment.wrongResultWords.size(); i++){
            data.add(mainDb.getNativeWordByForeign(CheckFragment.wrongResultWords.get(i)));
        }
        return data;
    }

    private class AddWordsToDectionary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            for(int i = 0; i < listId.size(); i++){
                String word = CheckFragment.wrongResultWords.get(listId.get(i));
                list.add(word);
            }

            for(int i = 0; i < list.size(); i++){
                mainDbForUser.update(Tables.getTableMain(), MainDbForUser.OWN, mainDb.getIdForeginWord(list.get(i)));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String message;

            Log.e("list", String.valueOf(list));

            if(list.size() == 1){
                message =  list.size() + " " + "слово" + " Добавлено в Словарь";
            }else if(list.size() == 2 || list.size() == 3 || list.size() == 4){
                message = list.size() + " " + "слова" + " Добавлено в Словарь";
            }else if(list.size() == 0){
                message = "Нет Слов Для Добавления";
            }else {
                message = list.size() + " " + "слов" + " Добавлено в Словарь";
            }

            utils.myToast(getActivity(),getActivity().getLayoutInflater(),getView(),
                    message, Toast.LENGTH_SHORT);

            utils.toolTitle(getActivity(), getResources().getString(R.string.navigation_item_1));
            utils.transactions(getFragmentManager(), new LessonTenWordFragment(), Utils.LESSON_TEN_WORDS_FRAGMENT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.resultbutton:
                new AddWordsToDectionary().execute();
                break;
        }
    }
}