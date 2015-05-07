package com.kuhnp.moneytransfertchallenge.fragment;

import android.content.Context;
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
 * Created by pierre on 28/04/2015.
 */
public class MoneyExchangeFragment extends Fragment {

    private Context mContext;
    List<String> mCurrencyArray;

    private EditText mEditTextSend;
    private EditText mEditTextReceived;
    private Spinner mSpinner1;
    private Spinner mSpinner2;
    private Button mSendButton;
    private InputMethodManager mImm;
    private boolean isReadyToSend = false;
    private boolean isReverse;
    private RestManager mRestManager;

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

        mRestManager = ((MyApplication)getActivity().getApplication()).restManager;
        mCurrencyArray = mRestManager.mCurrencyList;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, mCurrencyArray);
        mSpinner1.setAdapter(arrayAdapter);
        mSpinner2.setAdapter(arrayAdapter);

        mEditTextSend.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});
        mEditTextReceived.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        mImm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);


        mEditTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendButton.setText("CALCULATE");
                isReadyToSend = false;
            }
        });
        mEditTextReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendButton.setText("CALCULATE");
                isReadyToSend = false;
            }
        });

        mEditTextSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if (mEditTextReceived != null) {
                        mEditTextReceived.getText().clear();
                    }
                    mSendButton.setText("CALCULATE");
                    isReadyToSend = false;
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
                    mSendButton.setText("CALCULATE");
                    isReadyToSend = false;
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
                   Conversion conversion = new Conversion(mEditTextSend.getText().toString(),
                           mEditTextReceived.getText().toString(),
                           mSpinner2.getSelectedItem().toString(),
                           mSpinner1.getSelectedItem().toString(),
                           ((MainActivity)getActivity()).mContactSelected);
                   mRestManager.sendMoney(conversion);
               } else {
                   if (mEditTextSend.getText().toString().isEmpty() && mEditTextReceived.getText().toString().isEmpty()) {
                       Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
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
                mSendButton.setText("CALCULATE");
                isReadyToSend = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditTextReceived.getText().clear();
                mSendButton.setText("CALCULATE");
                isReadyToSend = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return viewRoot;

    }

    public void refreshFragment(Conversion conversion, boolean order){
        mSendButton.setText("SEND");
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

}

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

