package com.example.doan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_splash);
            Log.d(TAG, "SplashActivity started");

            // Delay and navigate
            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToStartScreen, SPLASH_DELAY);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            navigateToStartScreen();
        }
    }

    private void navigateToStartScreen() {
        try {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
