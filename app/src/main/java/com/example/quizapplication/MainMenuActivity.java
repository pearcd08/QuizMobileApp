package com.example.quizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapplication.Models.Quiz;
import com.example.quizapplication.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private TextView tvUsername;
    private Button btnLogout, btnFilter, btnCreate;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference quizRef, userRef;
    private String userName, userID, admin;
    private RecyclerView recyclerView;
    QuizList_Adapter quizAdapter;
    private Long now;
    private ArrayList<Quiz> quizArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_menu);
        //Get current time
        now = System.currentTimeMillis();
        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();
        //Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quizzes");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        //TextViews
        tvUsername = (TextView) findViewById(R.id.tv_menu_username);
        //Buttons
        btnFilter = findViewById(R.id.btn_playermenu_filter);
        btnLogout = findViewById(R.id.btn_playermenu_logout);
        btnCreate = findViewById(R.id.btn_playermenu_create);
        btnFilter.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        //Recycler View
        recyclerView = findViewById(R.id.rv_menu_quizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnCreate.setVisibility(View.GONE);
        //start

        getUser();
        getQuizzes();





    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnFilter.getId()) {
            displayFilterMenu(v);
        }
        if (v.getId() == btnLogout.getId()) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
        }
        if (v.getId() == btnCreate.getId()) {
            startActivity(new Intent(this, CreateQuizActivity.class));
        }
    }

    private void getQuizzes() {
        quizArrayList.clear();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Quiz quiz = ds.getValue(Quiz.class);
                    quizArrayList.add(quiz);
                }
                quizAdapter = new QuizList_Adapter(quizArrayList, admin);
                recyclerView.setAdapter(quizAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainMenuActivity.this, "Could not connect to database", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void displayFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.filter_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter_all:
                getQuizzes();
                return true;
            case R.id.menu_filter_ongoing:
                getOngoingQuizzes();
                return true;
            case R.id.menu_filter_finished:
                getPastQuizzes();
                return true;
            case R.id.menu_filter_upcoming:
                getUpcomingQuizzes();
                return true;
            case R.id.menu_filter_participated:
                getParticipatedQuizzes();
                ;
                return true;
            default:
                return false;

        }
    }

//

    private void getUser() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("uid").getValue().equals(userID)) {
                        User user = ds.getValue(User.class);

                        admin = user.getAdmin();
                        if(admin.equals("true")){
                            btnCreate.setVisibility(View.VISIBLE);
                        }
                        tvUsername.setText(user.getUsername());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainMenuActivity.this, "Could not load user", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            }
        });

    }

    private void getOngoingQuizzes() {
        ArrayList<Quiz> ongoingQuizzes = new ArrayList();
        for (int i = 0; i < quizArrayList.size(); i++) {
            Quiz quiz = quizArrayList.get(i);
            Long startDate = quiz.getStartDateTime();
            Long endDate = quiz.getEndDateTime();
            if (startDate <= now && endDate >= now) {
                ongoingQuizzes.add(quiz);

            }
        }
        quizAdapter = new QuizList_Adapter(ongoingQuizzes, admin);
        recyclerView.setAdapter(quizAdapter);
        Toast.makeText(this, "ongoing", Toast.LENGTH_SHORT).show();
    }

    private void getUpcomingQuizzes() {
        ArrayList<Quiz> upcomingQuizzes = new ArrayList();
        for (int i = 0; i < quizArrayList.size(); i++) {
            Quiz quiz = quizArrayList.get(i);
            Long startDate = quiz.getStartDateTime();
            if (startDate > now) {
                upcomingQuizzes.add(quiz);

            }
        }
        quizAdapter = new QuizList_Adapter(upcomingQuizzes, admin);
        recyclerView.setAdapter(quizAdapter);
    }

    private void getPastQuizzes() {
        ArrayList<Quiz> pastQuizzes = new ArrayList();
        for (int i = 0; i < quizArrayList.size(); i++) {
            Quiz quiz = quizArrayList.get(i);
            Long endDate = quiz.getEndDateTime();
            if (endDate < now) {
                pastQuizzes.add(quiz);

            }
        }
        quizAdapter = new QuizList_Adapter(pastQuizzes, admin);
        recyclerView.setAdapter(quizAdapter);
    }

    private void getParticipatedQuizzes() {
        ArrayList<Quiz> participatedQuizzes = new ArrayList();
        for (int i = 0; i < quizArrayList.size(); i++) {
            Quiz quiz = quizArrayList.get(i);
            String quizID = quizArrayList.get(i).getQuizID();
            quizRef.child(quizID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Leaderboard").child(userID).exists()) {
                        participatedQuizzes.add(quiz);
                        quizAdapter = new QuizList_Adapter(participatedQuizzes, admin);
                        recyclerView.setAdapter(quizAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        quizArrayList.clear();
        getQuizzes();
        getUser();

    }




}
