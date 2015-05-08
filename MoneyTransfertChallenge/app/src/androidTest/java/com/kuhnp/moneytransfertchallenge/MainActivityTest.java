package com.kuhnp.moneytransfertchallenge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.ListView;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

/**
 * Created by pierre
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private FloatingActionButton mFloatingActionButton;
    private MainActivity mMainActivity;
    private ListView mListView;

    public MainActivityTest(){
        super(MainActivity.class);
    }

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = getActivity();
        mFloatingActionButton = (FloatingActionButton) mMainActivity.findViewById(R.id.contactB);
        mListView = (ListView) mMainActivity.findViewById(R.id.fragmentContact_LV);
    }

    @MediumTest
    public void testClickContactButton_layout(){
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mFloatingActionButton);
    }

    public void testContactList_layout(){
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mListView);
        assertNotNull(mListView);
    }


}
