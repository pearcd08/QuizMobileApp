package com.example.quizapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizapplication.Models.Question;
import com.example.quizapplication.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Edit Text
        etEmail = (EditText) findViewById(R.id.txt_login_email);
        etPassword = (EditText) findViewById(R.id.txt_login_password);
        //Buttons
        btnLogin = (Button) findViewById(R.id.btn_login_login);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btn_login_register);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            login();
        }
        if (v.getId() == btnRegister.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

//        String email = "test@test.com";
//        String password = "password";


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.child("uid").getValue().equals(userID)) {
                                    User newUser = ds.getValue(User.class);
                                    if (newUser.isAdmin()) {
                                        startActivity(new Intent(MainActivity.this, AdminMenuActivity.class));
                                    } else{
                                        startActivity(new Intent(MainActivity.this, PlayerMenuActivity.class));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}







