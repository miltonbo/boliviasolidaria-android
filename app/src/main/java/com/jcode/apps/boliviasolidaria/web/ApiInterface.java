package com.jcode.apps.boliviasolidaria.web;

import com.jcode.apps.boliviasolidaria.entity.HelpRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST("solicitudes")
    Call<ServerResponse> sendHelpRequest(@Body HelpRequest helpRequest);

    @Multipart
    @POST("solicitudes/4/ci-foto")
    Call<String> sendPhotoCI(@Part MultipartBody.Part filePart);

    @POST("solicitudes/{id}/ci-foto")
    Call<String> sendPhotoCI2(@Path("id") long id,@Body BodyFile bodyFile);

}
