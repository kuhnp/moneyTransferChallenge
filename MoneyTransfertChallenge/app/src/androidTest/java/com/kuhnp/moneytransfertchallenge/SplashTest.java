package com.kuhnp.moneytransfertchallenge;


import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.kuhnp.moneytransfertchallenge.rest.RestManager;

/**
 * Created by pierre
 */
public class SplashTest extends ActivityInstrumentationTestCase2<Splash> {

    Splash mSplash;
    TextView mFirstTestText;
    RestManager mRestManager;

    public SplashTest(){
        super(Splash.class);
    }

    public SplashTest(Class<Splash> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);

        mSplash = getActivity();
        mFirstTestText = (TextView) mSplash.findViewById(R.id.app_title);
    }

    public void testPreconditions() {
        assertNotNull("mFirstTestText is null", mFirstTestText);
        assertEquals("Fast Transfer", mFirstTestText.getText().toString());
    }
}
