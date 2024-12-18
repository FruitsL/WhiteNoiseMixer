package com.example.whitenoisemixer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViewById(R.id.close_button).setOnClickListener(v -> {
            finish(); // 액티비티 닫기
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 닫을 때 애니메이션
        });

    }
}