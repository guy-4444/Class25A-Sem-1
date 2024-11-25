package com.guyi.class25a_sem_1;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.guyi.simplegraph.SnakeView;
import com.guyi.sushislider.Sushi;

import java.util.Random;

// https://androidexample365.com/
// https://android-arsenal.com/


public class MainActivity extends AppCompatActivity {
    int val = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        final SnakeView snakeView = (SnakeView)findViewById(R.id.snake);
        snakeView.setMinValue(0);
        snakeView.setMaxValue(100);

        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                Random random = new Random();
                int delta = random.nextInt(30) - 14;
                if (delta >= 0) {
                    snakeView.setStrokeColor(Color.GREEN);
                } else {
                    snakeView.setStrokeColor(Color.RED);
                }
                val += delta;
                val = Math.min(val, 100);
                val = Math.max(val, 1);
                snakeView.addValue(val);

                System.out.println("myHandler: here!"); // Do your work here
                handler.postDelayed(this, delay);
            }
        }, delay);

    }
}