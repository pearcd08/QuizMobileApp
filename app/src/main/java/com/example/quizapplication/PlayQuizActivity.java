package com.example.quizapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizapplication.Models.Question;
import com.example.quizapplication.Models.Score;
import com.example.quizapplication.Models.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private ImageButton btnLike, btnDislike;
    private TextView tvQuestion, tvScore;
    private String answer, quizID;
    private int questionNo = 0;
    private int quizScore = 0;
    private FirebaseDatabase database;
    private DatabaseReference quizRef, userRef;
    private ArrayList<Question> questionArray = new ArrayList<>();
    private View popupView;
    private LayoutInflater inflater;

    //Timer
    Chronometer timer;
    Boolean resume = false;
    long gameTime;
    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quizzes");
        userRef = database.getReference("Users");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            quizID = extras.getString("quizID");
            getQuestions();
        }
        if (extras == null) {
            Toast.makeText(this, "ERROR: Couldn't Load Quiz", Toast.LENGTH_SHORT).show();
        }


        //Buttons
        btnOption1 = findViewById(R.id.btn_quiz_option1);
        btnOption2 = findViewById(R.id.btn_quiz_option2);
        btnOption3 = findViewById(R.id.btn_quiz_option3);
        btnOption4 = findViewById(R.id.btn_quiz_option4);
        btnNext = findViewById(R.id.btn_quiz_next);


        btnOption1.setOnClickListener(this);
        btnOption2.setOnClickListener(this);
        btnOption3.setOnClickListener(this);
        btnOption4.setOnClickListener(this);
        btnNext.setOnClickListener(this);


        //TextViews
        tvQuestion = (TextView) findViewById(R.id.tv_quiz_question);
        tvScore = (TextView) findViewById(R.id.tv_quiz_score);
        
        //Timer
        timer = findViewById(R.id.timer);
        startTimer();
        timer.start();

        //Integers
        btnNext.setEnabled(false);
        tvScore.setVisibility(View.INVISIBLE);

    }

    private void startTimer() {
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                if (!resume) {
                    long minutes = ((SystemClock.elapsedRealtime() - timer.getBase())/1000) / 60;
                    long seconds = ((SystemClock.elapsedRealtime() - timer.getBase())/1000) % 60;
                    gameTime = SystemClock.elapsedRealtime();
                } else {
                    long minutes = ((gameTime - timer.getBase())/1000) / 60;
                    long seconds = ((gameTime - timer.getBase())/1000) % 60;
                    gameTime = gameTime + 1000;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnOption1.getId()) {
            String selection = btnOption1.getText().toString();
            if (selection.equals(answer)) {
                btnOption1.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore(true);

            } else {
                btnOption1.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();

            }
        }
        if (v.getId() == btnOption2.getId()) {
            String selection = btnOption2.getText().toString();
            if (selection.equals(answer)) {
                btnOption2.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore(true);
            } else {
                btnOption2.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }
        if (v.getId() == btnOption3.getId()) {
            String selection = btnOption3.getText().toString();
            if (selection.equals(answer)) {
                btnOption3.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore(true);
            } else {
                btnOption3.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }
        if (v.getId() == btnOption4.getId()) {
            String selection = btnOption4.getText().toString();
            if (selection.equals(answer)) {
                btnOption4.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore(true);
            } else {
                btnOption4.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }

        if (v.getId() == btnNext.getId()) {
            if (btnNext.getText().equals("FINISH")) {
                timer.stop();
                openLikeMenu(v);

            } else {
                btnNext.setEnabled(false);
                questionNo++;
                nextQuestion(questionNo);

            }

        }


    }

    private void getQuestions() {

        quizRef.child(quizID).child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    Log.d("QUESTIONS",question.getQuestion().toString());
                    questionArray.add(question);
                }
                nextQuestion(questionNo);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nextQuestion(int i) {
        //enable the score
        if ((i == 0)) {
            tvScore.setVisibility(View.VISIBLE);
        }
        //change the next button's text to finish
        if (i == 9) {
            btnNext.setText("FINISH");
        }
        //set the question text
        String fixQuestion = questionArray.get(i).getQuestion().replaceAll("&quot;","\"");
        tvQuestion.setText(fixQuestion);
        //set the answer
        answer = questionArray.get(i).getCorrectAnswer();
        Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
        //put answers into an array list
        ArrayList<String> answersList = new ArrayList<>();
        answersList.add(questionArray.get(i).getAnswer1());
        answersList.add(questionArray.get(i).getAnswer2());
        answersList.add(questionArray.get(i).getAnswer3());
        answersList.add(questionArray.get(i).getAnswer4());
        //shuffle list
        Collections.shuffle(answersList);
        //set the text of the buttons to the shuffled answer list
        btnOption1.setText(answersList.get(0));
        btnOption2.setText(answersList.get(1));
        btnOption3.setText(answersList.get(2));
        btnOption4.setText(answersList.get(3));
        //set the color of each button to reset it
        btnOption1.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption2.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption3.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption4.setBackgroundColor(getResources().getColor(R.color.blue));

    }


    private void getAnswer() {

        if (answer.equals(btnOption1.getText().toString())) {
            btnOption1.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (answer.equals(btnOption2.getText().toString())) {
            btnOption2.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (answer.equals(btnOption3.getText().toString())) {
            btnOption3.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (answer.equals(btnOption4.getText().toString())) {
            btnOption4.setBackgroundColor(getResources().getColor(R.color.green));
        }
        updateScore(false);

    }

    private void openLikeMenu(View view) {

        inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.like_quiz, null);

        btnLike = popupView.findViewById(R.id.btn_quiz_like);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();

            }
        });
        btnDislike = popupView.findViewById(R.id.btn_quiz_dislike);
        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikePost();
            }
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }


    private void updateScore(Boolean result) {
        if(result){
            quizScore++;
        }

        tvScore.setText(String.valueOf(quizScore)+"/"+(questionNo+1));
        btnNext.setEnabled(true);

    }



    private void likePost() {
        quizRef.child(quizID)
                .child("likes")
                .setValue(ServerValue.increment((1)));
        Toast.makeText(this, "liked", Toast.LENGTH_SHORT).show();
        leaderboard();
        startActivity(new Intent(this, PlayerMenuActivity.class));

    }

    private void dislikePost() {
        quizRef.child(quizID)
                .child("dislikes")
                .setValue(ServerValue.increment((1)));
        Toast.makeText(this, "disliked", Toast.LENGTH_SHORT).show();
        leaderboard();
        startActivity(new Intent(this, PlayerMenuActivity.class));

    }

    private void leaderboard(){
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = currentFirebaseUser.getUid();
        final String[] username = {""};



        userRef.child(userid). addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username[0] = user.getUsername();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        Score score = new Score(userid, username[0], quizScore, gameTime);
        quizRef.child(quizID)
                .child("Leaderboard")
                .child(userid)
                .setValue(score);

    }

//    Collections.sort(persons, Comparator.comparing(Person::getFname)
//            .thenComparingInt(Person::getAge));


}


