package com.kuhnp.moneytransfertchallenge;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kuhnp.moneytransfertchallenge.adapter.ContactCursorAdapter;
import com.kuhnp.moneytransfertchallenge.fragment.MoneyExchangeFragment;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import java.util.HashSet;
import java.util.Set;



public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "MainActivity";

    private boolean isContactsVisible = false;
    private ContactCursorAdapter mAdapter;
    private ListView mContactsList;
    public String mContactSelected = "";
    public String mEmailSelected = "";
    private FloatingActionButton mContactButton;
    public ProgressDialog mProgressDialog;
    private TextView mContactTV;
    private TextView mTitleTV;
    public ImageView mAvatarIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactsList = (ListView) findViewById(R.id.fragmentContact_LV);
        mContactsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mContactTV = (TextView) findViewById(R.id.contact_selected_TV);
        mTitleTV = (TextView) findViewById(R.id.send_money_to_text);
        mAvatarIV = (ImageView) findViewById(R.id.Contact_photo_IV);
        mAdapter = new ContactCursorAdapter(this, null, 0);
        mContactsList.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(0, null, MainActivity.this);
        mContactButton = (FloatingActionButton) findViewById(R.id.contactB);

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isContactsVisible)
                    showContactList();
                else
                    hideContactList();
                }
        });

        mAvatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isContactsVisible)
                    showContactList();
                else
                    hideContactList();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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

    /**
     * Update contact name and UI.
     * @param name
     */
    public void updateContactName(String name){
        this.mTitleTV.setText(R.string.contact_selected);
        this.mContactSelected = name;
        this.mContactTV.setVisibility(View.VISIBLE);
        this.mContactTV.setText(name);
    }

    public void updateEmail(String email){
        this.mEmailSelected = email;
    }

    /**
     * Update avatar Picture
     * @param thumbnail
     */
    public void updateContactAvatar(Bitmap thumbnail){
        this.mAvatarIV.setVisibility(View.VISIBLE);
        this.mContactButton.setVisibility(View.GONE);
        if(thumbnail!= null)
            this.mAvatarIV.setImageBitmap(thumbnail);
        else
            this.mAvatarIV.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.unknown_person));
    }

    @Override
    public void onBackPressed() {
        if(isContactsVisible){
            hideContactList();
        }
    }

    /**
     * Hide contact list method.
     */
    public void hideContactList(){
        mContactsList.post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_out_bottom);
                mContactsList.startAnimation(animation);
                mContactsList.setVisibility(View.GONE);
                isContactsVisible = false;
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Show contact list method.
     */
    public void showContactList(){
        mContactsList.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
        mContactsList.startAnimation(animation);
        isContactsVisible = true;
    }
}
