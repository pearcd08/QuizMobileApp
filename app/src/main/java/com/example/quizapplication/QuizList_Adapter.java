package com.example.quizapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapplication.Models.Quiz;

import java.util.ArrayList;

public class QuizList_Adapter extends RecyclerView.Adapter<QuizList_Holder> {
    private ArrayList<Quiz> mQuizArrayList;


    public QuizList_Adapter(ArrayList<Quiz> quizArrayList) {
        mQuizArrayList = quizArrayList;
    }

    @NonNull
    @Override
    public QuizList_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.quiz_card, parent, false);
        return new QuizList_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizList_Holder holder, int position) {
        holder.tv_Name.setText(mQuizArrayList.get(position).getName());

    }



    @Override
    public int getItemCount() {
        return mQuizArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}