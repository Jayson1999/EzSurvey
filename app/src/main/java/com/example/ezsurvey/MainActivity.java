package com.example.ezsurvey;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MainActivity extends AppCompatActivity {

    public static int SPLASH_TIME_OUT=4500;
    private ImageView splashImg,splashLoad;
    private TextView splashTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar and set to FULL SCREEN
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        splashTxt = (TextView)findViewById(R.id.splashTV);
        splashImg = (ImageView)findViewById(R.id.splash);
        splashLoad = (ImageView)findViewById(R.id.splashLoad);
        Glide.with(MainActivity.this).load(R.drawable.smile).apply(new RequestOptions().override(400,400)).into(splashImg);
        Glide.with(MainActivity.this).load(R.drawable.loading).apply(new RequestOptions().override(400)).into(splashLoad);

        //Hide the title bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        //initialize a zooming animation based on animation resources from anim
        Animation hyperspaceJump = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom);
        //Conduct animation
        splashTxt.startAnimation(hyperspaceJump);

        //Provide short delay of time based on SPLASH_TIME_OUT value before Navigating to Main Page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
