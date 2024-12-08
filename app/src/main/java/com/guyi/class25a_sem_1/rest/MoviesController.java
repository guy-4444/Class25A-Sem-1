package com.guyi.class25a_sem_1.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesController {

    private static final String BASE_URL = "https://pastebin.com/";

    private CallBack_Movies callBackMovies;
    private CallBack_Movie callBackMovie;

    private MoviesController setCallBackMovies(CallBack_Movies callBackMovies) {
        this.callBackMovies = callBackMovies;
        return this;
    }

    private void setCallBackMovie(CallBack_Movie callBackMovie) {
        this.callBackMovie = callBackMovie;
    }

    private MoviesApi getAPI() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MoviesApi moviesAPI = retrofit.create(MoviesApi.class);

        return moviesAPI;
    }

    public void fetchAllMovies(CallBack_Movies callBackMovies) {
        setCallBackMovies(callBackMovies);

        Call<List<Movie>> call = getAPI().loadMovies();

        call.enqueue(allMoviewCallBack);
    }

    public void fetchMovieById(String movieId, CallBack_Movie callBackMovie) {
        setCallBackMovie(callBackMovie);

        Call<Movie> call = getAPI().loadMovieByKey(movieId);

        call.enqueue(oneMovieCallBack);
    }

    private Callback<Movie> oneMovieCallBack = new Callback<Movie>() {

        @Override
        public void onResponse(Call<Movie> call, Response<Movie> response) {
            callBackMovie.ready(response.body());
        }

        @Override
        public void onFailure(Call<Movie> call, Throwable throwable) {
            callBackMovie.failed(throwable.getMessage());
        }
    };

    private Callback<List<Movie>> allMoviewCallBack = new Callback<List<Movie>>() {
        @Override
        public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
            callBackMovies.ready(response.body());
        }

        @Override
        public void onFailure(Call<List<Movie>> call, Throwable throwable) {
            callBackMovies.failed(throwable.getMessage());
        }
    };

    public interface CallBack_Movies {
        void ready(List<Movie> movies);
        void failed(String message);
    }

    public interface CallBack_Movie {
        void ready(Movie movie);
        void failed(String message);
    }

}