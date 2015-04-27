package com.kuhnp.moneytransfertchallenge.rest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by pierre on 27/04/2015.
 */
public interface RestApi {

    @GET("/currencies")
    public void getCurrencies(Callback<List<String>> response);
}
