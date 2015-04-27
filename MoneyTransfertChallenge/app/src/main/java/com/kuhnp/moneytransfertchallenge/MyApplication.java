package com.kuhnp.moneytransfertchallenge;

import android.app.Application;

import com.kuhnp.moneytransfertchallenge.rest.RestManager;

/**
 * Created by pierre on 27/04/2015.
 */
public class MyApplication extends Application {

    public RestManager restManager;

    @Override
    public void onCreate() {
        super.onCreate();
        restManager = RestManager.getInstance(getApplicationContext());
    }
}
