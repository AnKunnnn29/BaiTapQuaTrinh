package com.example.doan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.Utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        MaterialButton startButton = findViewById(R.id.btn_start);
        SessionManager sessionManager = new SessionManager(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    //  đã login -> chuyển đến trang Main
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Chuyển qua trang welcome
                    Intent intent = new Intent(StartActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }
}
