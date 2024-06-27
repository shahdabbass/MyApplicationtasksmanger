package com.example.myapplication_tasksmanger;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //start next activity (screen) automatically  after period of time
        Handler h = new Handler();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //to open new activity from current to next activity
                Intent i = new Intent(SplashScreen.this, SignInActivity.class);

                startActivity(i);
                //to close current activity
                finish();
            }
        };
        h.postDelayed(r, 3000);
    }



    }
