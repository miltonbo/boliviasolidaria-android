package com.jcode.apps.boliviasolidaria.web;

import com.jcode.apps.boliviasolidaria.entity.HelpRequest;
import com.jcode.apps.boliviasolidaria.entity.PointDistribution;
import com.jcode.apps.boliviasolidaria.entity.PointRequest;
import com.jcode.apps.boliviasolidaria.entity.Receptor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST("solicitudes")
    Call<ServerResponse> sendHelpRequest(@Body HelpRequest helpRequest);

    @POST("solicitudes/{id}/ci-foto")
    Call<String> sendPhotoCI2(@Path("id") long id,@Body BodyFile bodyFile);

    @GET("solicitudes/puntos")
    Call<List<PointRequest>> listPointRequests();

    @GET("solicitudes/{id}")
    Call<HelpRequest> gethelpRequest(@Path("id") long id);

    @GET("puntos-acopio/puntos")
    Call<List<PointDistribution>> listPointCenters();

    @GET("receptores-donacion")
    Call<List<Receptor>> listReceptores();

    @GET("receptores-donacion/{id}/logo")
    Call<String> getReceptorPhoto(@Path("id") long id);

}
