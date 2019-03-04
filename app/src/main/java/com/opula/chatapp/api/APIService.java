package com.opula.chatapp.api;

import com.opula.chatapp.notifications.MyResponse;
import com.opula.chatapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHaQZ8oE:APA91bFwwFxgwOy8esVr7VV7znLJugqM-2VOHjINpE0TFrKN4ZUD3sK8d2mKe3Id_g8cMTqTjI1tTV0b4Wgo4Kj0mq-oUzNxOIelSeH3bV4hY7hr-JNwr9P3UHY1pMRlXlmc_pbqNd5-"


            }
    )
//                    "Authorization:key=AAAAJqhgrTY:APA91bHBrAsGVQIXkFrTexWwrKVud7oodchZ0wyqxdZRP5A48eI4O56IU5gk_9ODjVxmNCv35aKmqH_7z6EgNa0Z_2XsFXoLmjyRigeGSrK-P8wdulkTDHAkKEAPY4qlKEtX0yyBsV4X"

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
