package com.kuhnp.moneytransfertchallenge.rest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.Conversion;
import com.kuhnp.moneytransfertchallenge.MainActivity;
import com.kuhnp.moneytransfertchallenge.MyApplication;
import com.kuhnp.moneytransfertchallenge.R;
import com.kuhnp.moneytransfertchallenge.fragment.MoneyExchangeFragment;

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
    public List<String> mCurrencyList;


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

    public void requestDataCurrencies(Context c){
        final Context context =c;
        mApi.getCurrencies(new Callback<List<String>>() {
            @Override
            public void success(List<String> strings, Response response) {
                for (String s : strings) {
                    Log.d(TAG, s);
                }
                mCurrencyList = strings;
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestDataConverion(String amount, String sendCurrency, String receivedCurency, final Context c, boolean order){
        final boolean orderTmp = order;
        mApi.getConversion(amount, sendCurrency, receivedCurency, new Callback<Conversion>() {
            @Override
            public void success(Conversion conversion, Response response) {
                updateFragment(conversion, c, orderTmp);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "error");
            }
        });
    }

    public void sendMoney(Conversion conversion){
        mApi.sendMoney(conversion, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if(response.getStatus() == 201 && response.getReason().equalsIgnoreCase("Created"))
                    Toast.makeText(context, "Money successfully transfered", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "error during the transaction", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateFragment(Conversion conversion, Context c, boolean order){
       MoneyExchangeFragment fragment = (MoneyExchangeFragment) ((MainActivity)c).getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.refreshFragment(conversion, order);


    }

}
