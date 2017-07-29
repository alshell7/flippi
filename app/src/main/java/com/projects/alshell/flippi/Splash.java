package com.projects.alshell.flippi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left);
            }
        }, 1000);
    }

}
