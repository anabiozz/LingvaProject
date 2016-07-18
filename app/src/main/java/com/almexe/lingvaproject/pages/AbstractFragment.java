package com.almexe.lingvaproject.pages;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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

public class AbstractFragment extends Fragment{

    //public static MainDbForUser mainDbForUser;
    //public static UserDb userDb;

    private VKApiUser user;
    private String[] scope = new String[]{VKScope.WALL, VKScope.PHOTOS};

    //public static MainDb mainDb;
    //protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* NavigationView navigationView = (NavigationView)findViewById(R.id.navigation);
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        ImageView image = (ImageView) headerView.findViewById(R.id.headerImageView);
        TextView headerName = (TextView) headerView.findViewById(R.id.headerName);
        TextView headerLastName = (TextView) headerView.findViewById(R.id.headerLastName);
        ImageView imageViewVk = (ImageView) headerView.findViewById(R.id.headerImageVk);*/
    }



    /********************************************************************************************/


    /*************************************************************************************/


    /**********************************************************************************************/
   /* void Vkloginlogout() {
        Driver.imageViewVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VKSdk.isLoggedIn()){

                    *//* Creating dialog*//*
                    *//******************************************************************//*
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
                    *//*************************************************************//*
                }else{
                    VKSdk.login(AbstractFragment.this, scope);
                }
            }
        });
    }*/

    void showDialog() {}
}
