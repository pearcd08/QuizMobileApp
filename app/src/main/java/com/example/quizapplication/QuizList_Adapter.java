package com.example.quizapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapplication.Models.Quiz;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizList_Adapter extends RecyclerView.Adapter<QuizList_Holder> {
    private final ArrayList<Quiz> mQuizArrayList;
    String mAdmin;
    Long now = System.currentTimeMillis();


    public QuizList_Adapter(ArrayList<Quiz> quizArrayList, String admin) {

        mQuizArrayList = quizArrayList;
        mAdmin = admin;
    }

    @NonNull
    @Override
    public QuizList_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_card, parent, false);
        return new QuizList_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizList_Holder holder, int position) {
        holder.btn_Play.setVisibility(View.GONE);
        holder.btn_Update.setVisibility(View.GONE);
        String quizID = mQuizArrayList.get(position).getQuizID();
        Long startDate = mQuizArrayList.get(position).getStartDateTime();
        Long endDate = mQuizArrayList.get(position).getEndDateTime();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quizRef = database.getReference("Quizzes");


        holder.tv_Name.setText("Quiz Name: " + mQuizArrayList.get(position).getName());
        holder.tv_Category.setText("Category: " + mQuizArrayList.get(position).getCategory());
        holder.tv_Difficulty.setText("Difficulty: " + mQuizArrayList.get(position).getDifficulty());
        holder.tv_StartDate.setText("Start Date: " + mQuizArrayList.get(position).getStartDate());
        holder.tv_EndDate.setText("End Date: " + mQuizArrayList.get(position).getEndDate());
        holder.tv_Likes.setText("Likes: " + mQuizArrayList.get(position).getLikes());

        //if user is an admin enable the update button
        if (mAdmin.equals("true")) {
            holder.btn_Update.setVisibility(View.VISIBLE);
            holder.btn_Update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UpdateQuizActivity.class);
                    intent.putExtra("quizID", quizID);
                    v.getContext().startActivity(intent);

                }
            });

        }
        //if user is a player and the start date is before now and the end date is after now
        else if (startDate <= now && endDate >= now) {
            quizRef.child(quizID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Leaderboard").child(userID).exists()) {
                        holder.btn_Play.setVisibility(View.GONE);

                    } else if (!snapshot.child("Leaderboard").child(userID).exists()) {
                        holder.btn_Play.setVisibility(View.VISIBLE);
                        holder.btn_Play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), PlayQuizActivity.class);
                                intent.putExtra("quizID", quizID);
                                v.getContext().startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }


    @Override
    public int getItemCount() {
        return mQuizArrayList.size();
    }


}