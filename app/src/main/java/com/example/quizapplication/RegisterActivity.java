package com.example.quizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizapplication.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etUsername,  etPassword;
    private Button btnSave, btnBack;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase
        fAuth = FirebaseAuth.getInstance();

        //Edittext
        etEmail = (EditText) findViewById(R.id.txt_register_email);
        etUsername = (EditText) findViewById(R.id.txt_register_username);
        etEmail = (EditText) findViewById(R.id.txt_register_email);
        etPassword = (EditText) findViewById(R.id.txt_register_password);

        //Buttons
        btnSave = (Button) findViewById(R.id.btn_register_save);
        btnSave.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btn_register_back);
        btnBack.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSave.getId()) {
            registerUser();
        } else if (v.getId() == btnBack.getId()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }

    private void registerUser() {

        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        Boolean isAdmin = false;


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email address");
            etEmail.requestFocus();
            return;
        }


        if (username.isEmpty()) {
            etUsername.setError("Please enter a First Name");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please provide a password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() <= 6) {
            etPassword.setError("Password must be atleast 6 characters");
            etPassword.requestFocus();
            return;
        }

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String UID = fAuth.getCurrentUser().getUid();
                            //set admin in firebase
                            User user = new User(UID, email, username, "false");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(fAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference("users")
                                                    .child(fAuth.getCurrentUser().getUid());
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(i);


                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Registration Error!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication Error!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }
}