package com.example.quizapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quizapplication.Models.Quiz;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class QuizList_Adapter extends FirebaseRecyclerAdapter<Quiz, QuizList_Holder> {




    public QuizList_Adapter(@NonNull FirebaseRecyclerOptions<Quiz> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuizList_Holder holder, int position, @NonNull Quiz model) {
        holder.tv_Name.setText(model.getName());
        holder.tv_Category.setText(model.getCategory());
        holder.tv_Difficulty.setText(model.getDifficulty());
        holder.tv_StartDate.setText(model.getStartDate());
        holder.tv_EndDate.setText(model.getEndDate());

        holder.btn_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PlayQuizActivity.class);
                i.putExtra("quizID",model.getQuizID());
                v.getContext().startActivity(i);


            }
        });
        holder.btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), UpdateQuizActivity.class);
                i.putExtra("quizID",model.getQuizID());
                v.getContext().startActivity(i);

            }
        });


    }

    @NonNull
    @Override
    public QuizList_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View view = inflater.inflate(R.layout.quiz_card,
                parent, false);
        return new QuizList_Holder(view);
    }
}
