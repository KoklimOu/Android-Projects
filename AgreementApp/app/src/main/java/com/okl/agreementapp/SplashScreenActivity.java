package com.okl.agreementapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;

public class SplashScreenActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, AgreementActivity.class);
//            startActivity(intent, ActivityOptions.makeTaskLaunchBehind().toBundle());
            startActivity(intent, ActivityOptions.makeBasic().toBundle());
//            startActivity(intent);
            finish();
        }, 1000);
    }
}