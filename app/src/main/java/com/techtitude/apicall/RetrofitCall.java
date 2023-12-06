package com.techtitude.apicall;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitCall {

    @POST("user")
    Call<DataModel> sendData(@Body DataModel dataModel);


}
