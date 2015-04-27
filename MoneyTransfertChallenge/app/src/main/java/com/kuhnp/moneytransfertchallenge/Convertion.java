package com.kuhnp.moneytransfertchallenge;

/**
 * Created by pierre on 27/04/2015.
 */
public class Convertion {

    private float sendamount;
    private float receiveamount;
    private String receivecurrency;
    private String sendcurrency;

    public Convertion(float sendmount, float receiveamount, String receivecurrency, String sendcurrency){
        this.sendamount = sendmount;
        this.receivecurrency = receivecurrency;
        this.receiveamount = receiveamount;
        this.sendcurrency = sendcurrency;
    }
}
