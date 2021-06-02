package com.roma.android.sihmi.model.network;

import com.roma.android.sihmi.model.database.entity.notification.NotifResponse;
import com.roma.android.sihmi.model.database.entity.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendNotifService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqO4r5r0:APA91bG-XB7M8yv8hJlrXOAaZx2R1nBJraM7XrFFHCoQ2Zqhqq7PtAJrKdUuBvXjVe-q18Iu31a-RoHG6lVeJL6-HJTrf80CtXbRwa5ewE8NyODSBIhQrt2JUwv2Q6DhK2zzXjc7ORyg"
            }
    )

    @POST("fcm/send")
    Call<NotifResponse> sendNotification(@Body Sender body);
}
