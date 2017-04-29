package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;


import static java.lang.Thread.sleep;

/**
 * Created by Kevin on 1-Mar-17.
 */
public class LoadingActivity extends Activity {

    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        mProgress = (ProgressBar)findViewById(R.id.progressBar5);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mProgressStatus <= 91) {

                    try {
                        sleep(20);
                        mProgressStatus = mProgressStatus + 1;
                    }catch(Exception e){

                    } finally {
                        if(mProgressStatus == 92)
                        {
                            Intent intent = new Intent(LoadingActivity.this, SplashActivity.class);
                            startActivity(intent);
                        }
                        // Update the progress bar
                        mHandler.post(new Runnable() {
                            public void run() {
                                mProgress.setProgress((int)(Math.sin(mProgressStatus * 3.141592 / 180) * 101));
                            }
                        });
                    }
                }

            }
        }).start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}