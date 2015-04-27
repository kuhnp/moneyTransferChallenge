package com.kuhnp.moneytransfertchallenge.rest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.Convertion;
import com.kuhnp.moneytransfertchallenge.MyApplication;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pierre on 27/04/2015.
 */
public class RestManager {

    public static final String TAG = "RestManager";
    public static final String ENDPOINT = "https://wr-interview.herokuapp.com/api";

    static RestManager mInstance;
    private Context context;
    private MyApplication application;
    private RestAdapter mRestAdapter;
    private RestApi mApi;


    private RestManager(Context context){
        this.context = context;
        application = (MyApplication) context.getApplicationContext();
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        mApi = mRestAdapter.create(RestApi.class);
    }

    public static RestManager getInstance(Context context){
        if(mInstance == null)
            mInstance = new RestManager(context);
        return mInstance;
    }

    public void requestDataCurrencies(){
        mApi.getCurrencies(new Callback<List<String>>() {
            @Override
            public void success(List<String> strings, Response response) {
                for (String s : strings) {
                    Log.d(TAG, s);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestDataConverion(){
        mApi.getConversion(new Callback<Convertion>() {
            @Override
            public void success(Convertion convertion, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
