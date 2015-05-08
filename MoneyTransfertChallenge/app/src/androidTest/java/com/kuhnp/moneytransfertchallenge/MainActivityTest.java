package com.kuhnp.moneytransfertchallenge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.method.Touch;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuhnp.moneytransfertchallenge.fragment.MoneyExchangeFragment;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.util.Random;

/**
 * Created by pierre
 */
    public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private FloatingActionButton mFloatingActionButton;
    private MainActivity mMainActivity;
    private ListView mListView;
    private ImageView mAvatarIV;
    private TextView mContactSelectedTV;
    private MoneyExchangeFragment mFragment;

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
        mAvatarIV = (ImageView) mMainActivity.findViewById(R.id.Contact_photo_IV);
        mContactSelectedTV = (TextView) mMainActivity.findViewById(R.id.contact_selected_TV);
        mFragment = (MoneyExchangeFragment) mMainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @MediumTest
    public void testContactButton_layout(){
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mFloatingActionButton);
    }

    public void testClickContactButton(){
        TouchUtils.clickView(this, mFloatingActionButton);
        assertTrue(View.VISIBLE == mListView.getVisibility());
        TouchUtils.clickView(this, mFloatingActionButton);
        assertTrue(View.GONE == mListView.getVisibility());
    }

    public void testClickContactInList(){
        TouchUtils.clickView(this, mFloatingActionButton);
        TouchUtils.clickView(this, mListView.getChildAt(new Random().nextInt(mListView.getChildCount())));
        assertTrue(View.GONE == mListView.getVisibility());
    }

    public void testContactList_layout(){
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mListView);
        assertNotNull(mListView);
        assertTrue(View.GONE == mListView.getVisibility());
        assertTrue(View.GONE == mAvatarIV.getVisibility());
        assertTrue(View.GONE == mContactSelectedTV.getVisibility());
    }

    public void testFragment_layout(){
        final View decorView = mMainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, mFragment.getView());
        assertTrue(View.VISIBLE == mFragment.mEditTextSend.getVisibility());
        assertTrue(View.VISIBLE == mFragment.mEditTextReceived.getVisibility());
        assertTrue(View.VISIBLE == mFragment.mSpinner1.getVisibility());
        assertTrue(View.VISIBLE == mFragment.mSpinner2.getVisibility());
    }

    public void testFragmentEditTextClick(){
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFragment.mEditTextSend.setText("1234");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, mFragment.mEditTextReceived);
        assertTrue(mFragment.mEditTextSend.getText().toString().isEmpty());

        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFragment.mEditTextReceived.setText("1234");
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, mFragment.mEditTextSend);
        assertTrue(mFragment.mEditTextReceived.getText().toString().isEmpty());
    }

    public void testCalculateButtonWithNoAmounts(){
        TouchUtils.clickView(this, mFragment.mSendButton);
        assertTrue(mFragment.mToast.getView().isShown());
    }

    public void testSendButtonWithNoContactSelected(){
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFragment.mEditTextSend.setText("1234");
                mFragment.mEditTextReceived.setText("1234");
                mFragment.isReadyToSend = true;
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, mFragment.mSendButton);
        assertTrue(mFragment.mToast.getView().isShown());
    }


}
