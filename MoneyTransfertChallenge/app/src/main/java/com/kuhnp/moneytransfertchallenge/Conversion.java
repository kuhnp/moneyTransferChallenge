package com.kuhnp.moneytransfertchallenge;

/**
 * Created by pierre on 27/04/2015.
 */
public class Conversion {

    private String sendamount;
    private String receiveamount;
    private String receivecurrency;
    private String sendcurrency;
    private String recipient;

    public Conversion(String sendmount, String receiveamount, String receivecurrency, String sendcurrency, String name){
        this.sendamount = sendmount;
        this.receivecurrency = receivecurrency;
        this.receiveamount = receiveamount;
        this.sendcurrency = sendcurrency;
        this.recipient = name;
    }

    public String getSendamount() {
        return sendamount;
    }

    public String getSendcurrency() {
        return sendcurrency;
    }

    public String getReceivecurrency() {
        return receivecurrency;
    }

    public String getReceiveamount() {
        return receiveamount;
    }
}
