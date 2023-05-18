package com.example.appjava.retrofit;


import io.reactivex.rxjava3.core.Observable;
import com.example.appjava.model.NotiResponse;
import com.example.appjava.model.NotiSenData;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNofication {
    @Headers( {
        "Content-Type: application/json",
            "Authorization: key=AAAAdDSBAHs:APA91bFYruG0LcgS7MG9ngZxhBsFISXKAFrceyi7xCtcQb4qMrzwjjUbHdlwI1-b_5dfC4UUANRzEVUUFLqPz5DF7-2FOZ6-GRkcll3FvjJO7PbkvH8Who1ZL3_pyoplxjrHkm9-NVkL"
    }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNofitication(@Body NotiSenData data);

}
