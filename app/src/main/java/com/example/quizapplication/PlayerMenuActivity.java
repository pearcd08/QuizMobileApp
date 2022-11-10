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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayerMenuActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private TextView tvUsername;
    private Button btnLogout, btnFilter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference quizRef, userRef;
    private String userName, userID;
    private RecyclerView recyclerView;
    QuizList_Adapter quizAdapter;
    private Boolean isAdmin;
    private Long now;
    private ArrayList<Quiz> quizArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_menu);
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
        btnFilter.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        //Recycler View
        recyclerView = findViewById(R.id.rv_menu_quizzes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizList_Adapter(quizArrayList);
        //Get current time
        now = System.currentTimeMillis();
        //start
        getUser();
        getQuizzes();
        //filterRecyclerView("ongoing");
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();


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
    }

    private void getQuizzes() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Quiz quiz = new Quiz();
                    quiz = ds.getValue(Quiz.class);
                    quizArrayList.add(quiz);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayerMenuActivity.this, "Could not connect to database", Toast.LENGTH_SHORT).show();

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
                        userName = ds.child("username").getValue(String.class);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayerMenuActivity.this, "Could not load user", Toast.LENGTH_LONG).show();
                startActivity(new Intent(PlayerMenuActivity.this, MainActivity.class));
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
        quizAdapter = new QuizList_Adapter(ongoingQuizzes);
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
        quizAdapter = new QuizList_Adapter(upcomingQuizzes);
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
        quizAdapter = new QuizList_Adapter(pastQuizzes);
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
                        Toast.makeText(PlayerMenuActivity.this, "found player", Toast.LENGTH_SHORT).show();
                        participatedQuizzes.add(quiz);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        quizAdapter = new QuizList_Adapter(participatedQuizzes);
    }


}
