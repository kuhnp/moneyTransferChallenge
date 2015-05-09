package com.kuhnp.moneytransfertchallenge.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kuhnp.moneytransfertchallenge.Conversion;
import com.kuhnp.moneytransfertchallenge.MainActivity;
import com.kuhnp.moneytransfertchallenge.MyApplication;
import com.kuhnp.moneytransfertchallenge.R;
import com.kuhnp.moneytransfertchallenge.rest.RestManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pierre
 */
public class MoneyExchangeFragment extends Fragment {

    private Context mContext;
    public List<String> mCurrencyArray;

    public EditText mEditTextSend;
    public EditText mEditTextReceived;
    public Spinner mSpinner1;
    public Spinner mSpinner2;
    public Button mSendButton;
    private TextView mResultTV;
    private Button mNewTransferB;
    private InputMethodManager mImm;
    public boolean isReadyToSend = false;
    private boolean isReverse;
    private RestManager mRestManager;
    public Toast mToast;

    public MoneyExchangeFragment(Context context){
        this.mContext = context;
    }

    public MoneyExchangeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.money_exchange_fragment, container, false);

        mEditTextSend = (EditText) viewRoot.findViewById(R.id.amount1);
        mEditTextReceived = (EditText) viewRoot.findViewById(R.id.amount2);
        mSpinner1 = (Spinner) viewRoot.findViewById(R.id.currencySpinner1);
        mSpinner2 = (Spinner) viewRoot.findViewById(R.id.currencySpinner2);
        mSendButton = (Button) viewRoot.findViewById(R.id.sendB);
        mResultTV = (TextView) viewRoot.findViewById(R.id.money_send_TV);
        mNewTransferB = (Button) viewRoot.findViewById(R.id.new_transfer_B);


        mRestManager = ((MyApplication)getActivity().getApplication()).restManager;
        mCurrencyArray = mRestManager.mCurrencyList;

        if(mCurrencyArray != null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, mCurrencyArray);
            mSpinner1.setAdapter(arrayAdapter);
            mSpinner2.setAdapter(arrayAdapter);
        }

        mEditTextSend.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});
        mEditTextReceived.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        mImm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);


        mEditTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReadyToSendFalse();
            }
        });
        mEditTextReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReadyToSendFalse();
            }
        });

        mEditTextSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if (mEditTextReceived != null) {
                        mEditTextReceived.getText().clear();
                    }
                    setReadyToSendFalse();
                }
            }
        });

        mEditTextReceived.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if (!mEditTextSend.getText().toString().isEmpty()) {
                        mEditTextSend.getText().clear();
                    }
                    setReadyToSendFalse();
                }
            }
        });

        mEditTextSend.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    isReverse = false;
                    requestConversion();
                    return false;
                }
                return false;
            }
        });

        mEditTextReceived.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    isReverse = true;
                    requestConversion();
                    return true;
                }
                return false;
            }
        });

       mSendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (isReadyToSend) {
                   if(!((MainActivity)getActivity()).mContactSelected.isEmpty()) {
                       final Conversion conversion = new Conversion(mEditTextSend.getText().toString(),
                               mEditTextReceived.getText().toString(),
                               mSpinner2.getSelectedItem().toString(),
                               mSpinner1.getSelectedItem().toString(),
                               ((MainActivity) getActivity()).mContactSelected);
                       new AlertDialog.Builder(getActivity()).setTitle("Send money ")
                               .setMessage("Are you sure you want to send "+ conversion.getSendamount()+" "+ conversion.getSendcurrency()+" to "+conversion.getRecipient()+"?")
                               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       ((MainActivity) getActivity()).mProgressDialog.show();
                                       mRestManager.sendMoney(conversion, getActivity());
                                   }
                               })
                               .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                   }
                               })
                               .setIcon(android.R.drawable.ic_dialog_alert)
                               .show();
                   }
                   else{
                       mToast = Toast.makeText(getActivity(), R.string.no_contact_selected, Toast.LENGTH_SHORT);
                       mToast.show();
                   }
               } else {
                   if (mEditTextSend.getText().toString().isEmpty() && mEditTextReceived.getText().toString().isEmpty()) {
                       mToast = Toast.makeText(getActivity(), R.string.empty_fields, Toast.LENGTH_SHORT);
                       mToast.show();
                   }
                   else{
                       if(mEditTextSend.getText().toString().isEmpty())
                           isReverse = true;
                       else
                           isReverse = false;
                       requestConversion();
                   }
               }
           }
       });

        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditTextReceived.getText().clear();
                setReadyToSendFalse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditTextReceived.getText().clear();
                setReadyToSendFalse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return viewRoot;

    }

    /**
     * Method to update UI (display values received after requesting the API) after conversion
     * @param conversion
     * @param order
     */
    public void refreshFragmentAfterConversion(Conversion conversion, boolean order){
        mSendButton.setText(R.string.send);
        isReadyToSend = true;
        mEditTextReceived.clearFocus();
        mEditTextSend.clearFocus();
        DecimalFormat df = new DecimalFormat("0.##");

        if(!order) {
            double res = Double.parseDouble(conversion.getReceiveamount());
            mEditTextReceived.setText(df.format(res));
            mEditTextSend.setText(conversion.getSendamount());
            mImm.hideSoftInputFromWindow(mEditTextSend.getWindowToken(), 0);
        }
        else{
            double res = Double.parseDouble(conversion.getReceiveamount());
            mEditTextReceived.setText(conversion.getSendamount());
            mEditTextSend.setText(df.format(res));
            mImm.hideSoftInputFromWindow(mEditTextReceived.getWindowToken(), 0);
        }
        ((MainActivity)getActivity()).mProgressDialog.dismiss();
    }

    /**
     * Method to update UI after money is sent
     * @param conversion
     */
    public void refreshFragmentAfterSend(Conversion conversion){
        mSendButton.setVisibility(View.GONE);
        mResultTV.setVisibility(View.VISIBLE);
        mNewTransferB.setVisibility(View.VISIBLE);
        mNewTransferB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
        mEditTextSend.setFocusable(false);
        mEditTextReceived.setFocusable(false);
        mSpinner1.setEnabled(false);
        mSpinner2.setEnabled(false);
        ((MainActivity)getActivity()).mAvatarIV.setClickable(false);
        ((MainActivity)getActivity()).mProgressDialog.dismiss();


    }

    public void requestConversion(){
        ((MainActivity) getActivity()).mProgressDialog.show();
        if(!isReverse){
            mRestManager.requestDataConverion(mEditTextSend.getText().toString(), mSpinner1.getSelectedItem().toString(),
                    mSpinner2.getSelectedItem().toString(), getActivity(), false);
        }
        else{
            mRestManager.requestDataConverion(mEditTextReceived.getText().toString(), mSpinner2.getSelectedItem().toString(),
                    mSpinner1.getSelectedItem().toString(), getActivity(), true);
        }
    }

    public void setReadyToSendFalse(){
        mSendButton.setText(R.string.calculate);
        isReadyToSend = false;
    }
}

/**
 * Filter Class to limit the user input to 2 decimal digits
 */
class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
        mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}
