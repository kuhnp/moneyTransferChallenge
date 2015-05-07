package com.kuhnp.moneytransfertchallenge.rest;

import com.kuhnp.moneytransfertchallenge.Conversion;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by pierre on 27/04/2015.
 */
public interface RestApi {

    @GET("/currencies")
    public void getCurrencies(Callback<List<String>> response);

    @GET("/calculate")
    public void getConversion(
            @Query("amount") String amount,
            @Query("sendcurrency") String sendCurrency,
            @Query("receivecurrency") String receiveCurrency,
            Callback<Conversion> response);

    @POST("/send")
    public void sendMoney(@Body Conversion conversion, Callback<String> response);

}
