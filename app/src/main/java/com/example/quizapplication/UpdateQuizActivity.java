package com.example.quizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapplication.Models.Quiz;
import com.google.android.gms.common.internal.Objects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_startDate, tv_endDate;
    private EditText et_quizName;
    private Button btn_cancel, btn_update, btn_delete;
    private ImageButton btn_startDate, btn_endDate;
    private String quizName, quizStartDate, quizEndDate, quizID;
    private Long quizStartDateMS, quizEndDateMS;
    private FirebaseDatabase database;
    private DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_quiz);

        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quizzes");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            quizID = extras.getString("quizID");
            getQuiz();
        }
        if (extras == null) {
            Toast.makeText(this, "ERROR: Couldn't Load Quiz", Toast.LENGTH_SHORT).show();
        }


        et_quizName = findViewById(R.id.txt_updatequiz_quizname);
        tv_startDate = findViewById(R.id.tv_updatequiz_startdate);
        tv_endDate = findViewById(R.id.tv_updatequiz_enddate);

        btn_cancel = findViewById(R.id.btn_updatequiz_cancel);
        btn_update = findViewById(R.id.btn_updatequiz_update);
        btn_delete = findViewById(R.id.btn_updatequiz_delete);

        btn_startDate = findViewById(R.id.btn_updatequiz_startdate);
        btn_endDate = findViewById(R.id.btn_updatequiz_enddate);

        btn_cancel.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_startDate.setOnClickListener(this);
        btn_endDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn_cancel.getId()) {
            startActivity(new Intent(this, MainMenuActivity.class));
        }
        if (v.getId() == btn_update.getId()) {
            updateQuiz();
        }
        if (v.getId() == btn_delete.getId()) {
            deleteQuiz();
        }
        if (v.getId() == btn_startDate.getId()) {
            selectDate("start");

        }
        if (v.getId() == btn_endDate.getId()) {
            selectDate("end");
        }
        if (v.getId() == btn_cancel.getId()) {
            startActivity(new Intent(this, MainMenuActivity.class));
        }
    }

    private void deleteQuiz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm");
        builder.setMessage("Delete "+quizName+"?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quizRef.child(quizID).removeValue();
                        Toast.makeText(UpdateQuizActivity.this, "Successfully deleted "+quizName, Toast.LENGTH_SHORT).show() ;
                        startActivity(new Intent(UpdateQuizActivity.this, MainMenuActivity.class));

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getQuiz() {

        quizRef.child(quizID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Quiz quiz = snapshot.getValue(Quiz.class);
                    quizName = quiz.getName();
                    et_quizName.setText(quiz.getName(), TextView.BufferType.EDITABLE);
                    tv_startDate.setText(quiz.getStartDate());
                    tv_endDate.setText(quiz.getEndDate());
                    quizStartDateMS = quiz.getStartDateTime();
                    quizEndDateMS = quiz.getEndDateTime();
                }
                else{
                    Toast.makeText(UpdateQuizActivity.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateQuizActivity.this, MainMenuActivity.class));
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void updateQuiz() {
        String newName = et_quizName.getText().toString();
        String newStartDate = tv_startDate.getText().toString();
        String newEndDate = tv_endDate.getText().toString();


        if (newName.isEmpty()) {
            et_quizName.setError("Please enter the quiz name");
            et_quizName.requestFocus();
            return;
        }

        if (newStartDate.isEmpty()) {
            Toast.makeText(this, "Select a start Date", Toast.LENGTH_SHORT).show();
            btn_startDate.requestFocus();
            return;
        }

        if (newEndDate.isEmpty()) {
            Toast.makeText(this, "Select a end Date", Toast.LENGTH_SHORT).show();
            btn_endDate.requestFocus();
            return;
        }
        if (quizStartDateMS != null && quizEndDateMS != null && quizStartDateMS > quizEndDateMS){
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            btn_endDate.requestFocus();
            return;

        }
        else{
            quizRef.child(quizID).child("name").setValue(newName);
            quizRef.child(quizID).child("startDate").setValue(newStartDate);
            quizRef.child(quizID).child("endDate").setValue(newEndDate);
            quizRef.child(quizID).child("startDateTime").setValue(quizStartDateMS);
            quizRef.child(quizID).child("endDateTime").setValue(quizEndDateMS);
            Toast.makeText(this, "Quiz Updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainMenuActivity.class));

        }
    }


    private void selectDate(String selection) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UpdateQuizActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (selection.equals("start")) {
                            tv_startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            quizStartDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = null;
                            try {
                                date = sdf.parse(quizStartDate);
                                quizStartDateMS = date.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                        } else if (selection.equals("end")) {
                            tv_endDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            quizEndDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = null;
                            try {
                                date = sdf.parse(quizEndDate);
                                quizEndDateMS = date.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                },
                year, month, day);
        datePickerDialog.show();

    }


}
