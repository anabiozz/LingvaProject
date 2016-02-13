package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.adapter.WordsListAdapter;
import com.almexe.lingvaproject.db.MainDb;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.db.UserDb;
import com.almexe.lingvaproject.utils.CircleTransform;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.Tables;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LearnedWordsFragment extends Fragment{

    MainDbForUser mainDbForUser;
    MainDb mainDb;

    private VKApiUser user;
    private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};
    private UserDb userDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_learned_words, container, false);

        mainDbForUser = new MainDbForUser(getActivity());
        mainDb = new MainDb(getActivity());
        userDb = new UserDb(getActivity());

        RecyclerView rv = (RecyclerView)v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new WordsListAdapter(createListData(),createNativeListData()));

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

                    logoutBtn.setOnClickListener(new View.OnClickListener() {
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

                    dontlogoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();

                }else{
                    VKSdk.login(LearnedWordsFragment.this, scope);
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

    private ArrayList<String> createListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).size(); i++){
           data.add( mainDb.getWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).get(i)));
        }
        return data;
    }

    private ArrayList<String> createNativeListData() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).size(); i++){
            data.add( mainDb.getNativeWordById(mainDbForUser.getListId(Tables.getTableMain(), MainDbForUser.LEARNED).get(i)));
        }
        return data;
    }
}
