package com.almexe.lingvaproject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.almexe.lingvaproject.db.MainDbForUser;
import com.almexe.lingvaproject.pages.AddOwnWordsFragment;
import com.almexe.lingvaproject.pages.LearnedWordsFragment;
import com.almexe.lingvaproject.pages.LessonTenWordFragment;
import com.almexe.lingvaproject.pages.OwnLessonFragment;
import com.almexe.lingvaproject.pages.Settings;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.CustomTypefaceSpan;
import com.almexe.lingvaproject.utils.InitialService;
import com.almexe.lingvaproject.utils.Tables;
import com.almexe.lingvaproject.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Driver extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final String TAG = "Driver";

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private NavigationView navigationView;

    Utils utils;

    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new Utils();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.navigation_item_1;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        // listen for navigation events
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);
        fontMenu();

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (null == savedInstanceState) {
            utils.toolTitle(this,"LingvaApp");
            selectItem(0);
            //showLogin();
        } else {
            selectItem(mNavItemId);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        ImageView image = (ImageView) headerView.findViewById(R.id.headerImageView);
        TextView headerName = (TextView) headerView.findViewById(R.id.headerName);
        TextView headerLastName = (TextView) headerView.findViewById(R.id.headerLastName);
        ImageView imageViewVk = (ImageView) headerView.findViewById(R.id.headerImageVk);

        Typeface mainFont = Typeface.createFromAsset(getAssets(), Constants.TYPEFONT);

        headerName.setTypeface(mainFont);
        headerLastName.setTypeface(mainFont);

        TextView allWords = (TextView)headerView.findViewById(R.id.allWords);
        TextView numberAllWords = (TextView)headerView.findViewById(R.id.numberAllWords);
        TextView learnedWords = (TextView)headerView.findViewById(R.id.learnedWords);
        TextView numberlLearnedWords = (TextView) headerView.findViewById(R.id.numberlLearnedWords);

        numberAllWords.setText(String.valueOf(Constants.NUMBERWORD));

        allWords.setTypeface(mainFont);
        learnedWords.setTypeface(mainFont);
        numberAllWords.setTypeface(mainFont);
        numberlLearnedWords.setTypeface(mainFont);
        if(Tables.getTableMain() != null) {
            //numberlLearnedWords.setText(String.valueOf(MainDbForUser.getInstance().getCountWordsFromTableWhereColumnEqualsOne(Tables.getTableMain(), MainDbForUser.LEARNED)));
        }else {
            numberlLearnedWords.setText(String.valueOf(0));
        }


        imageViewVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constants.ACTION_LOGIN_VK);
                sendBroadcast(intent);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, InitialService.class));
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopService(new Intent(this, InitialService.class));
        Log.d(TAG, "onStop");
    }

    public void fontMenu(){
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    public void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), Constants.TYPEFONT);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

        @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        // update highlighted item in the navigation menu
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectItem(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }
	
    public void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        
        switch (position) {

        case 0:
            //fragment = new StartFragment();
            break;

        case R.id.navigation_item_1:
            fragment = new LessonTenWordFragment();
            break;

        case R.id.navigation_item_2:
            fragment = new AddOwnWordsFragment();
            break;

        case R.id.navigation_item_3:
            fragment = new OwnLessonFragment();

               /* new Utils().myToast(getApplicationContext(),getLayoutInflater(),getCurrentFocus(),
                        "СЛОВАРЬ ПУСТ", Toast.LENGTH_SHORT);*/
            break;

        case R.id.navigation_item_4:
            fragment = new LearnedWordsFragment();
            break;

        case R.id.navigation_item_5:
            fragment = new Settings();
            break;
        default:
            break;
        }
        
        if (fragment != null) {
            utils.transactions(getFragmentManager(), fragment);
            MenuItem pos = navigationView.getMenu().findItem(position);
            CharSequence title = pos.getTitle();
            setTitle(title);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }
}
