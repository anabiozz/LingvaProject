package com.almexe.lingvaproject.pages;

import android.app.Fragment;
import android.os.Bundle;

import com.vk.sdk.VKScope;
import com.vk.sdk.api.model.VKApiUser;

public class BaseFragment extends Fragment{

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
                    VKSdk.login(BaseFragment.this, scope);
                }
            }
        });
    }*/

    void showDialog() {}
}
