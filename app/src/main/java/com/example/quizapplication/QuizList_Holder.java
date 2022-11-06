package com.example.quizapplication;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class QuizList_Holder extends RecyclerView.ViewHolder {
    public TextView tv_Name, tv_Category, tv_Difficulty, tv_Likes, tv_Players, tv_StartDate, tv_EndDate;
    public Button btn_Play, btn_Update, btn_Delete;

    public QuizList_Holder(@NonNull View itemView) {
        super(itemView);
        tv_Name = itemView.findViewById(R.id.tv_card_quizname);
        tv_Category= itemView.findViewById(R.id.tv_card_category);
        tv_Difficulty = itemView.findViewById(R.id.tv_card_difficulty);
        tv_StartDate = itemView.findViewById(R.id.tv_card_startDate);
        tv_EndDate = itemView.findViewById(R.id.tv_card_endDate);

        btn_Play = itemView.findViewById(R.id.btn_card_play);
        btn_Update = itemView.findViewById(R.id.btn_card_update);

    }
}
