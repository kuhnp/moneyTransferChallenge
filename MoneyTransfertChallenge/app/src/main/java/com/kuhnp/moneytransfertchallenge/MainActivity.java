package com.kuhnp.moneytransfertchallenge;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.adapter.ContactCursorAdapter;
import com.kuhnp.moneytransfertchallenge.rest.RestApi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String TAG = "MainActivity";

    private MyApplication application;

    private ContactCursorAdapter mAdapter;
    private ListView mContactsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactsList = (ListView) findViewById(R.id.fragmentContact_LV);
        mAdapter = new ContactCursorAdapter(this, null, 0);
        mContactsList.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(0, null, MainActivity.this);

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


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] args = null;

        String[] PROJECTION = new String[] {
                ContactsContract.RawContacts._ID,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract
                        .Contacts.DISPLAY_NAME_PRIMARY
                        : ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.PHOTO };

        String selection = ContactsContract.CommonDataKinds.Email.DATA
                + " NOT LIKE \"\"";


        String sortOrder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                    + " COLLATE LOCALIZED ASC";
        } else {
            sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";
        }

        return new CursorLoader(this,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION,
                selection, args, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // remove the duplicated email contact
        String[] PROJECTION = new String[] {
                ContactsContract.RawContacts._ID,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract
                        .Contacts.DISPLAY_NAME_PRIMARY
                        : ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA };

        MatrixCursor result = new MatrixCursor(PROJECTION);
        Set<String> seen = new HashSet<>();
        while (data.moveToNext()) {
            String raw = data.getString(3);
            if (!seen.contains(raw)) {
                seen.add(raw);
                result.addRow(new Object[] { data.getLong(0),
                        data.getString(1), data.getInt(2), data.getString(3) });
            }
        }
        mAdapter.swapCursor(result);
        mContactsList.setFastScrollEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mContactsList.setFastScrollAlwaysVisible(true);    // This method only exists from API level 11
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mContactsList.setFastScrollAlwaysVisible(false);    // This method only exists from API level 11
        }
        mContactsList.setFastScrollEnabled(false);          // We need to make sure the ListView does not try and use an indexer that does not exist yet
        mAdapter.swapCursor(null);

    }
}
