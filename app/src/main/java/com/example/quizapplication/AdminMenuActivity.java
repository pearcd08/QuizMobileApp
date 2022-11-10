package com.example.quizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminMenuActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnCreateQuiz, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        btnCreateQuiz = findViewById(R.id.btn_adminmenu_createquiz);
        btnCreateQuiz.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==btnCreateQuiz.getId()){
            startActivity(new Intent(AdminMenuActivity.this, CreateQuizActivity.class));
        }
    }
}