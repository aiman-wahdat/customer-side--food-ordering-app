package com.example.smdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class splashScreen extends AppCompatActivity {

    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim);
        logo.startAnimation(animation);
        SharedPreferences sPref = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        boolean isLogin = sPref.getBoolean("isLogin",false);
        String id = sPref.getString("userId","none");
       // Toast.makeText(this,"inSplash "+sPref.getBoolean("isLogin",false),Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (isLogin) {
                    current_user_singleton currentUser = current_user_singleton.getInstance();
                    currentUser.setUserId(id);
                    startActivity(new Intent(splashScreen.this,MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(splashScreen.this,LoginActivity.class));
                    finish();
                }

            }
        },2000);

    }

    public void init()
    {
        logo = findViewById(R.id.iv_logo);
    }
}