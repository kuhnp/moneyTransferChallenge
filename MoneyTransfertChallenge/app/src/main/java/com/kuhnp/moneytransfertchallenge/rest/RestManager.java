package com.kuhnp.moneytransfertchallenge.rest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.Conversion;
import com.kuhnp.moneytransfertchallenge.MainActivity;
import com.kuhnp.moneytransfertchallenge.R;
import com.kuhnp.moneytransfertchallenge.fragment.MoneyExchangeFragment;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pierre
 */
public class RestManager {

    public static final String TAG = "RestManager";
    public static final String ENDPOINT = "https://wr-interview.herokuapp.com/api";

    static RestManager mInstance;
    private RestAdapter mRestAdapter;
    private RestApi mApi;
    public List<String> mCurrencyList;

    private RestManager(){
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        mApi = mRestAdapter.create(RestApi.class);
    }

    public static RestManager getInstance(){
        if(mInstance == null)
            mInstance = new RestManager();
        return mInstance;
    }

    /**
     * Method to get get the available currencies from the API
     * @param c
     */
    public void requestDataCurrencies(Context c){
        final Context context =c;
        mApi.getCurrencies(new Callback<List<String>>() {
            @Override
            public void success(List<String> strings, Response response) {
                mCurrencyList = strings;
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
                ((Activity)context).finish();
            }
        });
    }

    /**
     * Method to call the API to calculate the conversion depending on the chosen currencies
     * @param amount
     * @param sendCurrency
     * @param receivedCurency
     * @param c
     * @param order
     */
    public void requestDataConverion(String amount, String sendCurrency, String receivedCurency, final Context c, boolean order){
        final boolean orderTmp = order;
        mApi.getConversion(amount, sendCurrency, receivedCurency, new Callback<Conversion>() {
            @Override
            public void success(Conversion conversion, Response response) {
                updateFragmentAfterConversion(conversion, c, orderTmp);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(c, R.string.network_error, Toast.LENGTH_SHORT).show();
                ((MainActivity)c).mProgressDialog.dismiss();
            }
        });
    }

    /**
     * Method to send Money to the API
     * @param conversion
     * @param c
     */
    public void sendMoney(final Conversion conversion, final Context c){
        mApi.sendMoney(conversion, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (response.getStatus() == 201 && response.getReason().equalsIgnoreCase("Created"))
                    updateFragmentAfterSend(conversion, c);
                else
                    Toast.makeText(c, R.string.transaction_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(c, R.string.network_error, Toast.LENGTH_SHORT).show();
                ((MainActivity)c).mProgressDialog.dismiss();
            }
        });
    }

    public void updateFragmentAfterConversion(Conversion conversion, Context c, boolean order){
        MoneyExchangeFragment fragment = (MoneyExchangeFragment) ((MainActivity)c).getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.refreshFragmentAfterConversion(conversion, order);
    }

    public void updateFragmentAfterSend(Conversion conversion, Context c){
        MoneyExchangeFragment fragment = (MoneyExchangeFragment) ((MainActivity)c).getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.refreshFragmentAfterSend(conversion);
    }
}
