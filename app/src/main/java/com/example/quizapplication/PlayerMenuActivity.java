package com.example.quizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayerMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUsername;
    private Button btnQuiz, btnCreateQuiz;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //TextViews
        tvUsername = (TextView) findViewById(R.id.tv_menu_username);
        //Buttons
        btnQuiz = (Button) findViewById(R.id.btn_menu_quiz);
        btnCreateQuiz = findViewById(R.id.btn_menu_createquiz);
        btnQuiz.setOnClickListener(this);
        btnCreateQuiz.setOnClickListener(this);



        getUser();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnQuiz.getId()) {
            startActivity(new Intent(PlayerMenuActivity.this, PlayQuizActivity.class));
        }
        if ( v.getId() == btnCreateQuiz.getId()){
            startActivity(new Intent(PlayerMenuActivity.this, CreateQuizActivity.class));
        }

    }

    private void getUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uID = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("uid").getValue().equals(uID)) {
                        String userName = ds.child("username").getValue(String.class);
                        Boolean isAdmin = ds.child("admin").getValue(Boolean.class);

                        if (isAdmin) {
                            tvUsername.setText(userName + " is an admin");
                            // load menu UI options for an admin
                            loadAdminUI();
                        } else {
                            tvUsername.setText(userName + " is a player");
                            // Load menu UI for a player
                            loadPlayerUI();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayerMenuActivity.this, "Could not load user", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadAdminUI() {

    }

    private void loadPlayerUI() {

    }


}
