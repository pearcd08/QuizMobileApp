package com.example.quizapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private TextView tvQuestion, tvScore;
    private String answer;
    private int score, questionNo;
    RequestQueue queue = null;


    public RequestQueue getRequestQueue(Context context) {
        Log.d("JSON", " getRequestQueue ");
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        return queue;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
        queue = getRequestQueue(getApplicationContext());


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
        tvQuestion  = (TextView) findViewById(R.id.tv_quiz_question);
        tvScore  = (TextView) findViewById(R.id.tv_quiz_score);

        //Integers
        score = 0;
        questionNo = 0;
        answer = "";

        btnNext.setEnabled(false);
        tvScore.setVisibility(View.INVISIBLE);


        getAPI(questionNo);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnOption1.getId()){
            String selection = btnOption1.getText().toString();
            if(selection == answer){
                score++;
                btnOption1.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore();

            }
            else{
                btnOption1.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();

            }
        }
        if (v.getId() == btnOption2.getId()){
            String selection = btnOption2.getText().toString();
            if(selection == answer){
                score++;
                btnOption2.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore();
            }
            else{
                btnOption2.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }
        if (v.getId() == btnOption3.getId()){
            String selection = btnOption3.getText().toString();
            if(selection == answer){
                score++;
                btnOption3.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore();
            }
            else{
                btnOption3.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }
        if (v.getId() == btnOption4.getId()){
            String selection = btnOption4.getText().toString();
            if(selection == answer){
                score++;
                btnOption4.setBackgroundColor(getResources().getColor(R.color.green));
                updateScore();
            }
            else{
                btnOption4.setBackgroundColor(getResources().getColor(R.color.red));
                getAnswer();
            }
        }

        if (v.getId() == btnNext.getId()){
            if(btnNext.getText().equals("FINISH")){
                startActivity(new Intent(this, PlayerMenuActivity.class));

            }
            else{
                btnNext.setEnabled(false);
                questionNo++;
                getAPI(questionNo);

            }

        }

    }

    private void getAnswer() {

        if(answer.equals(btnOption1.getText().toString())){
            btnOption1.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else if(answer.equals(btnOption2.getText().toString())){
            btnOption2.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else if(answer.equals(btnOption3.getText().toString())){
            btnOption3.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else if(answer.equals(btnOption4.getText().toString())){
            btnOption4.setBackgroundColor(getResources().getColor(R.color.green));
        }
        updateScore();

    }

    private void getAPI(int questionNo) {

        String url = "https://opentdb.com/api.php?amount=10&type=multiple";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray data = response.getJSONArray("results");
                            JSONObject questions = data.getJSONObject(questionNo);

                            String question = questions.getString("question");
                            String correctAnswer = questions.getString("correct_answer");

                            ArrayList<String> answers = new ArrayList<>();

                            JSONArray incorrectAnswers=questions.getJSONArray("incorrect_answers");


                            for(int j=0; j<incorrectAnswers.length(); j++){

                                answers.add(incorrectAnswers.get(j).toString());


                            }

                            answers.add(questions.getString("correct_answer"));

                            createQuestion(question, correctAnswer, answers);


                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error in parsing");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);


    }

    private void updateScore(){
        tvScore.setText(score+"/"+(questionNo+1));
        btnNext.setEnabled(true);

    }

    private void createQuestion(String question, String answer1, ArrayList<String> answers) {

        tvQuestion.setText(question);
        answer = answer1;

        if((questionNo == 0)){
            tvScore.setVisibility(View.VISIBLE);
        }

        if((questionNo) == 9){
            btnNext.setText("FINISH");
        }




        //create array of 1,2,3,4
        //grab a random number out of array


        btnOption1.setText(answers.get(0));
        btnOption2.setText(answers.get(1));
        btnOption3.setText(answers.get(2));
        btnOption4.setText(answers.get(3));

        btnOption1.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption2.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption3.setBackgroundColor(getResources().getColor(R.color.blue));
        btnOption4.setBackgroundColor(getResources().getColor(R.color.blue));


    }



}


