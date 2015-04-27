package com.kuhnp.moneytransfertchallenge;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.rest.RestApi;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {


    public static final String TAG = "MainActivity";
    public static final String ENDPOINT = "https://wr-interview.herokuapp.com/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        RestApi api = restAdapter.create(RestApi.class);

        api.getCurrencies(new Callback<List<String>>() {
            @Override
            public void success(List<String> strings, Response response) {

                for(String s : strings){
                    Log.d(TAG,s);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
