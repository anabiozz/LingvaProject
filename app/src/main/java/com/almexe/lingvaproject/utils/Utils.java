package com.almexe.lingvaproject.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.almexe.lingvaproject.Driver;
import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.pages.StartFragment;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Utils extends Fragment{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ADD_OWN_WORDS_FRAGMENT = "ADD_OWN_WORDS_FRAGMENT" ;
    public static final String CHECK_FRAGMENT = "CHECK_FRAGMENT" ;
    public static final String LEARNED_WORDS_FRAGMENT = "LEARNED_WORDS_FRAGMENT" ;
    public static final String LESSON_TEN_WORDS_FRAGMENT = "LESSON_TEN_WORDS_FRAGMENT" ;
    public static final String OWN_LESSON_FRAGMENT = "OWN_LESSON_FRAGMENT" ;
    public static final String WRONG_RESULT_FRAGMENT = "WRONG_RESULT_FRAGMENT" ;
    public static final String SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT" ;
    public static final String START_FRAGMENT = "START_FRAGMENT" ;
    public static final String EXAMPLES_SENTENCES_FRAGMENT = "EXAMPLES_SENTENCES_FRAGMENT" ;

    public void myToast(Context context, LayoutInflater inflater, View v,
                        String message, int toastLength){
        try{
            View layout = inflater.inflate(R.layout.toast, (ViewGroup) v.findViewById(R.id.toast_layout_root));
            TextView textViewText = (TextView) layout.findViewById(R.id.text);
            textViewText.setText(message);

            Toast toast = new Toast(context);
            toast.setDuration(toastLength);
            toast.setView(layout);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void toolTitle(Activity a, String title){
        Typeface font = Typeface.createFromAsset(a.getAssets(), Constants.TYPEFONT);
        SpannableString mNewTitle = new SpannableString(title);
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((Driver)a).setTitle(mNewTitle);
    }

    public void transactionsWithAnimation(FragmentManager fragmentManager, Fragment fragment, String tag){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        fragmentTransaction.replace(R.id.content_frame, fragment, tag).addToBackStack(tag).commit();
    }

    public void transactions(FragmentManager fragmentManager, Fragment fragment, String tag){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, tag).commit();
    }

    public static void updateText(Activity act, final String resID, int id) {
        final TextView loadingText = (TextView) act.findViewById(id);
        act.runOnUiThread(new Runnable()
        {
            public void run()
            {
                loadingText.setText(resID);

            }

        });
    }

    public void wallPost(Activity a, final String message){
        final StringBuilder builder = new StringBuilder().append(message);
        final Bitmap photo =  BitmapFactory.decodeResource(a.getResources(), R.drawable.coloronda);
        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, 0);
        request.executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {
                photo.recycle();
                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                VKRequest postRequest = VKApi.wall().post(VKParameters.from(
                        VKApiConst.MESSAGE, message + "\n",
                        VKApiConst.ATTACHMENTS, String.format("photo%s_%s", photoModel.owner_id, photoModel.id)));

                postRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                    }

                    @Override
                    public void onError(VKError error) {

                    }

                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {

                    }
                });
            }
            @Override
            public void onError(VKError error) {
            }
        });
    }
}
