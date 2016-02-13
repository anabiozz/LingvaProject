package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StartFragment extends Fragment{

    public static MainDbForUser mainDbForUser;
    public static UserDb userDb;
    public static final String KEY = "Key";

    private VKApiUser user;
    private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};

    private LinearLayout linlaHeaderProgress;
    public static MainDb mainDb;
    Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils = new Utils();

        /*dialog creating only one */
        if(utils.getPreference(getActivity(), KEY)){
            showDialog();
            utils.setPreference(getActivity(), false, KEY);
        }

        try {
            new GetDataFromDb().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_start, container, false);
        linlaHeaderProgress = (LinearLayout) v.findViewById(R.id.linlaHeaderProgress);

        /*Если залогированы то повляется веб-форма VK или же создаем/берем дефолтного юзера*/
        if(VKSdk.isLoggedIn())
            VKSdk.login(StartFragment.this, scope);
        else{
            try {
                new VkErrorResponse().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        /*Login/Logout Button*/
        Vkloginlogout();

        return v;
    }

    /*******************************************************************************************/

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
    /**********************************************************************************************/
    private void Vkloginlogout() {
        Driver.imageViewVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VKSdk.isLoggedIn()){

                    /* Creating dialog*/
                    /******************************************************************/
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
                    /*************************************************************/
                }else{
                    VKSdk.login(StartFragment.this, scope);
                }
            }
        });
    }
    /**********************************************************************************************/
    /*get logged user*/
    private class VkResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linlaHeaderProgress.setVisibility(View.VISIBLE);

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
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /********************************************************************************************/
    /*get default user*/
    private class VkErrorResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               linlaHeaderProgress.setVisibility(View.VISIBLE);
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

            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /*************************************************************************************/
    private class GetDataFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mainDb = new MainDb(getActivity());
            mainDbForUser = new MainDbForUser(getActivity());
            userDb = new UserDb(getActivity());

            try {
                mainDb.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }try {
                mainDb.openDataBase();
            } catch (Exception ioe) {
                throw new Error("Unable to open database");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /*******************************************************************************/
    /*Show alert dialog*/
    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.start_alert);

        TextView text = (TextView) dialog.findViewById(R.id.textStartDialog);
        //ImageView imageView = (ImageView) dialog.findViewById(R.id.startImageVk);
        Button button = (Button)dialog.findViewById(R.id.nothx);
        Button buttonYesVk = (Button)dialog.findViewById(R.id.yesVk);

        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFONT);

        text.setTypeface(type2);
        button.setTypeface(type2);
        buttonYesVk.setTypeface(type2);

        buttonYesVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(StartFragment.this, scope);
                dialog.cancel();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new VkErrorResponse().execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });

        dialog.show();
    }
}