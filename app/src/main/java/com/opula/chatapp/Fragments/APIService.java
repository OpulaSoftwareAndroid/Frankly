package com.opula.chatapp.Fragments;

import com.opula.chatapp.Notifications.MyResponse;
import com.opula.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AIzaSyB0vHp81Ua4seXfUUqRifQTztvGmv3DyXg"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
