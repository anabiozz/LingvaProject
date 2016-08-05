package com.almexe.lingvaproject;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Toast;

import com.almexe.lingvaproject.pages.AddOwnWordsFragment;
import com.almexe.lingvaproject.pages.LearnedWordsFragment;
import com.almexe.lingvaproject.pages.LessonTenWordFragment;
import com.almexe.lingvaproject.pages.OwnLessonFragment;
import com.almexe.lingvaproject.pages.Settings;
import com.almexe.lingvaproject.pages.StartFragment;
import com.almexe.lingvaproject.utils.Constants;
import com.almexe.lingvaproject.utils.CustomTypefaceSpan;
import com.almexe.lingvaproject.utils.InitialService;
import com.almexe.lingvaproject.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class Driver extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{


    public static final String TAG = "Driver";
    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private NavigationView navigationView;
    boolean mBounded;
    InitialService initialService;
    Utils utils;
    private static final String TWITTER_KEY = "ab3HXTn6fXUXVLEEubs5ZXV9Q";
    private static final String TWITTER_SECRET = "pJn6repBWuSYwnclFDzWTMBANQ3pdZ9I6Ph5CCRb58q3nktRtb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new Utils();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.navigation_item_1;
        } else {
            mNavItemId = savedInstanceState.getInt(Constants.NAV_ITEM_ID);
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

        mDrawerLayout.addDrawerListener(mDrawerToggle);

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
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            initialService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            InitialService.LocalBinder mLocalBinder = (InitialService.LocalBinder)service;
            initialService = mLocalBinder.getService();
            int countLearnedWords = initialService.getCountLearnedWords();
            Log.e(TAG, String.valueOf(countLearnedWords));
            Utils.updateText(Driver.this, String.valueOf(countLearnedWords), R.id.numberlLearnedWords);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent it = new Intent(this, InitialService.class);
        bindService(it, mConnection, BIND_AUTO_CREATE);
        Log.e("IRemote", "onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        Log.e("IRemote", "onStop");
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
        }, Constants.DRAWER_CLOSE_DELAY_MS);
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
        outState.putInt(Constants.NAV_ITEM_ID, mNavItemId);
    }
	
    public void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        
        switch (position) {

        case 0:
            //fragment = new StartFragment();
            //utils.transactions(getFragmentManager(), fragment, Utils.START_FRAGMENT);
            break;

        case R.id.navigation_item_1:
            fragment = new LessonTenWordFragment();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.LESSON_TEN_WORDS_FRAGMENT);
            break;

        case R.id.navigation_item_2:
            fragment = new AddOwnWordsFragment();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.ADD_OWN_WORDS_FRAGMENT);
            break;

        case R.id.navigation_item_3:
            fragment = new OwnLessonFragment();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.OWN_LESSON_FRAGMENT);
               /* new Utils().myToast(getApplicationContext(),getLayoutInflater(),getCurrentFocus(),
                        "СЛОВАРЬ ПУСТ", Toast.LENGTH_SHORT);*/
            break;

        case R.id.navigation_item_4:
            fragment = new LearnedWordsFragment();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.LEARNED_WORDS_FRAGMENT);
            break;

        case R.id.navigation_item_5:
            fragment = new Settings();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.SETTINGS_FRAGMENT);
            break;

        case R.id.navigation_item_6:
            fragment = new Settings();
            utils.transactionsWithAnimation(getFragmentManager(), fragment, Utils.SETTINGS_FRAGMENT);
            break;
        default:
            break;
        }
        
        if (fragment != null) {
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
