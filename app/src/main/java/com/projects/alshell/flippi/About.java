package com.projects.alshell.flippi;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.projects.alshell.flippi.slidr.Slidr;
import com.projects.alshell.flippi.slidr.model.SlidrConfig;
import com.projects.alshell.flippi.slidr.model.SlidrPosition;

public class About extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        SlidrConfig mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.RIGHT)
                .sensitivity(1f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(false)
                .edgeSize(0.58f)
                .build();
        Slidr.attach(this, mConfig);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left);
    }
}
