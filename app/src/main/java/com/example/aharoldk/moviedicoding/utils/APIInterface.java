package com.example.aharoldk.moviedicoding.utils;

import com.example.aharoldk.moviedicoding.pojo.Movie;
import com.example.aharoldk.moviedicoding.pojo.pojodetail.Detail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("search/movie")
    Call<Movie> getSearchMovie(@Query("api_key") String apiKey, @Query("language") String language, @Query("query") String query);

    @GET("movie/upcoming")
    Call<Movie> getUpcomingMovie(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{movie_id}")
    Call<Detail> getDetailMovie(@Path("movie_id") String movie_id, @Query("api_key") String apiKey, @Query("language") String language);

}
