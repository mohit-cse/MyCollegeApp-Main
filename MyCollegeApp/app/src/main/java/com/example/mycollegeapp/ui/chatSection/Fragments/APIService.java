package com.example.mycollegeapp.ui.chatSection.Fragments;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIService {

    @POST("fcm/send")
    Call<String> sendNotification(@HeaderMap HashMap<String,String> headers, @Body String body);

}
