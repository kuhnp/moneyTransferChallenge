package com.kuhnp.moneytransfertchallenge.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuhnp.moneytransfertchallenge.MainActivity;
import com.kuhnp.moneytransfertchallenge.R;

/**
 * Created by pierre on 27/04/2015.
 */
public class ContactCursorAdapter extends CursorAdapter {

    public static final String[] PHOTO_BITMAP_PROJECTION = new String[] { ContactsContract
            .CommonDataKinds.Photo.PHOTO };

    LayoutInflater mInflater;
    AlphabetIndexer mAlphaIndexer;
    private String mContactSeleceted;
    private String mEmailSelected;

    public  ContactCursorAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView =  mInflater.inflate(R.layout.adapter_phonecontact, null);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ContactHolder holder = (ContactHolder) view.getTag();
        if (holder == null) {
            holder = new ContactHolder();
            holder.name_TV = (TextView) view
                    .findViewById(R.id.adaptContact_name_TV);
            holder.icon_IV = (ImageView) view
                    .findViewById(R.id.adaptContact_photo_IV);
            holder.email_TV = (TextView) view
                    .findViewById(R.id.adaptContact_email_TV);
            holder.contact_CB = (CheckBox) view
                    .findViewById(R.id.adaptContact_CB);
            holder.general_L = view.findViewById(R.id.layout_general);
            view.setTag(holder);
        }


        final String name = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        final String email = cursor
                .getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        final Integer photoId=cursor.getInt(cursor
                .getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
        holder.email_TV.setText(email);
        holder.name_TV.setText(name);
        final Bitmap thumbnail = fetchThumbnail(cursor.getInt(cursor
                .getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
        if (thumbnail != null) {
            holder.icon_IV.setImageBitmap(thumbnail);
        }
        else {
            holder.icon_IV.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.unknown_person));
        }

        final ContactHolder holderTmp = holder;
        holder.general_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // contact chosen
                ((MainActivity)context).updateContactName(name);
                ((MainActivity)context).updateEmail(email);
                ((MainActivity)context).updateContactAvatar(thumbnail);

                holderTmp.general_L.setBackgroundColor(((MainActivity) mContext).getResources().getColor(R.color
                        .contact_list_highlight_color));
                holderTmp.contact_CB.setChecked(true);
                mContactSeleceted = name;
                mEmailSelected = email;
                ((MainActivity)context).hideContactList();
            }
        });

        if (mContactSeleceted != null) {
            if (mContactSeleceted.equalsIgnoreCase(name) && mEmailSelected.equalsIgnoreCase(email)) {
                holderTmp.contact_CB.setChecked(true);
                holderTmp.general_L.setBackgroundColor(((MainActivity) mContext).getResources().getColor(R.color
                        .contact_list_highlight_color));
            }
            else{
                holderTmp.contact_CB.setChecked(false);
                holderTmp.general_L.setBackgroundColor(((MainActivity) mContext).getResources().getColor(R.color
                        .background_material_light));
            }
        }

    }

    @Override
    public Cursor swapCursor(Cursor c) {
        // Create our indexer
        if (c != null) {
            String columnName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                columnName = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
            } else {
                columnName = ContactsContract.Contacts.DISPLAY_NAME;
            }
            mAlphaIndexer = new AlphabetIndexer(c, c.getColumnIndex(columnName), "#ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        return super.swapCursor(c);
    }

    final Bitmap fetchThumbnail(final int thumbnailId) {
        final Uri uri = ContentUris.withAppendedId(
                ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = mContext.getContentResolver().query(uri,
                PHOTO_BITMAP_PROJECTION, null, null, null);
        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(
                            thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        } finally {
            cursor.close();
        }
    }

    static class ContactHolder {
        TextView name_TV;
        TextView email_TV;
        ImageView icon_IV;
        CheckBox contact_CB;
        View general_L;
    }


}
