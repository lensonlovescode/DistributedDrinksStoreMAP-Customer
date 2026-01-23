package com.lensonmutugi.distributeddrinksstore_customer.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // ===== ORDER ROUTES =====
    @POST("order")
    Call<MpesaResponse> createOrder(@Body OrderRequest request);

    @POST("cashorder")
    Call<MpesaResponse> createCashOrder(@Body OrderRequest request);

    // ===== M-PESA ROUTES =====
    @POST("mpesapush")
    Call<MpesaResponse> sendStkPush(@Body MpesaRequest request);

    @GET("order-status/{checkoutRequestID}")
    Call<MpesaResponse> checkStatus(@Path("checkoutRequestID") String checkoutRequestID);
}
