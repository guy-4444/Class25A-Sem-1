package com.guyi.class25a_sem_1.rest;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guyi.class25a_sem_1.R;
import com.guyi.class25a_sem_1.databinding.ActivityRestBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Activity_Rest extends AppCompatActivity {

    private ActivityRestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.BTNDownload.setOnClickListener(v -> download());

        downloadUrl("https://pastebin.com/raw/tY5Q7Bv6");
    }

    private void downloadUrl(String url) {
        GenericController genericController = new GenericController(new GenericController.CallBack_Generic() {
            @Override
            public void success(String data) {
                int x = 0;
            }

            @Override
            public void error(String error) {
                int x = 0;
            }
        });
        genericController.fetchData(url);
    }


    private void download() {
        MoviesController moviesController = new MoviesController();
        moviesController.fetchMovieById("uWcaqqs9", new MoviesController.CallBack_Movie() {
            @Override
            public void ready(Movie movie) {
                int x = 0;
            }

            @Override
            public void failed(String message) {
                int x = 0;
            }
        });
    }

}