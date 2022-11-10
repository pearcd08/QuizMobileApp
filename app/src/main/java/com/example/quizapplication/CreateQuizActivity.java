package com.example.quizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizapplication.Models.Question;
import com.example.quizapplication.Models.Quiz;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CreateQuizActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner catSpinner;
    private final ArrayList<String> categoryNames = new ArrayList<>();
    private final ArrayList<Integer> categoryCodes = new ArrayList<>();
    private String quizName, quizCategory, quizDifficulty, quizURL, quizID, quizStartDate, quizEndDate;
    private Long quizStartDateMS, quizEndDateMS;
    private EditText et_QuizName;
    private TextView tvStartDate, tvEndDate;
    private Button btnEasy, btnMedium, btnHard, btnSaveQuiz;
    private ImageButton btnStartDate, btnEndDate;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        //Get Firebase Table "Users"
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Quizzes");
        //Start Request Queue
        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
        queue = getRequestQueue(getApplicationContext());
        //Widgets
        et_QuizName = findViewById(R.id.txt_createquiz_quizname);
        tvStartDate = findViewById(R.id.tv_createquiz_startdate);
        tvEndDate = findViewById(R.id.tv_createquiz_enddate);
        btnEasy = findViewById(R.id.btn_createquiz_easy);
        btnMedium = findViewById(R.id.btn_createquiz_medium);
        btnHard = findViewById(R.id.btn_createquiz_hard);
        btnStartDate = findViewById(R.id.btn_createquiz_startdate);
        btnEndDate = findViewById(R.id.btn_createquiz_enddate);
        btnSaveQuiz = findViewById(R.id.btn_createquiz_savequiz);
        catSpinner = findViewById(R.id.spinner_createquiz_categories);

        //Set onClick listeners
        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSaveQuiz.setOnClickListener(this);


        loadArray();
        loadSpinner();

    }

    public RequestQueue getRequestQueue(Context context) {
        Log.d("JSON", " getRequestQueue ");
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        return queue;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnEasy.getId()) {
            quizDifficulty = "easy";
            btnEasy.setBackgroundColor(Color.YELLOW);
            btnMedium.setBackgroundColor(Color.BLUE);
            btnHard.setBackgroundColor(Color.BLUE);
        }
        if (v.getId() == btnMedium.getId()) {
            quizDifficulty = "medium";
            btnEasy.setBackgroundColor(Color.BLUE);
            btnMedium.setBackgroundColor(Color.YELLOW);
            btnHard.setBackgroundColor(Color.BLUE);
        }
        if (v.getId() == btnHard.getId()) {
            quizDifficulty = "hard";
            btnEasy.setBackgroundColor(Color.BLUE);
            btnMedium.setBackgroundColor(Color.BLUE);
            btnHard.setBackgroundColor(Color.YELLOW);
        }
        if (v.getId() == btnStartDate.getId()) {
            selectDate("start");
        }
        if (v.getId() == btnEndDate.getId()) {
            selectDate("end");
        }
        if (v.getId() == btnSaveQuiz.getId()) {
            checkInputs();
        }

    }


    private void selectDate(String selection) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateQuizActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (selection.equals("start")) {
                            tvStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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
                            tvEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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


    private void checkInputs() {

        quizName = et_QuizName.getText().toString();

        if (quizName.isEmpty()) {
            Toast.makeText(this, "Please Enter  Quiz Name", Toast.LENGTH_SHORT).show();
        }
        if (quizCategory.isEmpty()) {
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
        }
        if (quizDifficulty.isEmpty()) {
            Toast.makeText(this, "Please Select Difficulty", Toast.LENGTH_SHORT).show();
        }
        if (quizStartDate.isEmpty()) {
            Toast.makeText(this, "Please Select Start Date", Toast.LENGTH_SHORT).show();
        }
        if (quizEndDate.isEmpty()) {
            Toast.makeText(this, "Please Select End Date", Toast.LENGTH_SHORT).show();

        }
        if(quizEndDateMS < quizStartDateMS){
            Toast.makeText(this, "End Date is before start date", Toast.LENGTH_SHORT).show();
        }
        else if (quizName != null && quizCategory != null && quizDifficulty != null &&
                quizStartDate != null && quizEndDate != null) {



            getAPI();

        }

    }




    public void getAPI() {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, createURLString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("results");
                            //Loop through each question in the API response
                            if (data != null && data.length() > 0) {
                                quizID = myRef.push().getKey();
                                Quiz quiz = new Quiz(quizID, quizName, quizCategory,
                                        quizDifficulty, quizStartDate, quizEndDate,
                                        quizStartDateMS, quizEndDateMS, 0, 0);


                                myRef.child(quizID).setValue(quiz);


                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject questions = data.getJSONObject(i);
                                    ArrayList<String> answers = new ArrayList<>();
                                    String questionString = questions.getString("question");

                                    String correctAnswer = questions.getString("correct_answer");
                                    JSONArray incorrectAnswers = questions.getJSONArray("incorrect_answers");
                                    for (int j = 0; j < incorrectAnswers.length(); j++) {
                                        answers.add(incorrectAnswers.get(j).toString());
                                    }
                                    answers.add(questions.getString("correct_answer"));
                                    //Create new Question
                                    Question q = new Question(questionString, answers.get(0), answers.get(1), answers.get(2), answers.get(3), correctAnswer);
                                    //Create a nest inside the Quiz id for Questions
                                    DatabaseReference quizRef = database.getReference("Quizzes").child(quizID).child("Questions");
                                    //Create a nest for each question in the loop
                                    quizRef.child("question" + i).setValue(q);
                                }
                                Toast.makeText(CreateQuizActivity.this, "Quiz Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CreateQuizActivity.this, MainMenuActivity.class));


                            }


                        } catch (JSONException e) {
                            Toast.makeText(CreateQuizActivity.this, "Couldn't get request from OpenTBD", Toast.LENGTH_SHORT).show();

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

    private String createURLString() {
        int index = 0;
        for (int i = 0; i < categoryNames.size(); i++) {
            if (categoryNames.get(i).equals(quizCategory)) {
                index = i;
                break;
            }
        }
        String categoryCode = categoryCodes.get(index).toString();
        Toast.makeText(CreateQuizActivity.this, categoryCode, Toast.LENGTH_SHORT).show();
        //TO DO
        //ADD QUIZ TYPE

        quizURL = "https://opentdb.com/api.php?amount=10&category=" + categoryCode + "&difficulty=" + quizDifficulty + "&type=multiple";
        Log.d("URL", quizURL);
        return quizURL;


    }

    private void loadArray() {
        //Codes for each category
        String c1 = "General Knowledge=9";
        String c2 = "Books=10";
        String c3 = "Movies=11";
        String c4 = "Music=12";
        String c5 = "Musicals & Theatre=13";
        String c6 = "Television=14";
        String c7 = "Video Games=15";
        String c8 = "Board Games=16";
        String c9 = "Science & Nature=17";
        String c10 = "Computers=18";
        String c11 = "Mathematics=19";
        String c12 = "Mythology=20";
        String c13 = "Sports=21";
        String c14 = "Geography=22";
        String c15 = "History=23";
        String c16 = "Politics=24";
        String c17 = "Art=25";
        String c18 = "Celebrities=26";
        String c19 = "Animals=27";
        String c20 = "Vehicles=28";
        String c21 = "Comic Books=29";
        String c22 = "Gadgets=30";
        String c23 = "Anime & Magna=31";
        String c24 = "Cartoons & Animation=32";
        String[] categories = {c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14,
                c15, c16, c17, c18, c19, c20, c21, c22, c23, c24};
        //Sort the array with A-Z sorting
        Arrays.sort(categories);
        //For each category in the array, split the Category Name and the Category Code
        //Then put them into their own arrays
        for (String string : categories) {
            String[] parts = string.split("=");
            String name = parts[0];
            int code = Integer.parseInt(parts[1]);
            categoryNames.add(name);
            categoryCodes.add(code);
        }
        //Test to see the arrays match the codes
        for (int i = 0; i < categoryCodes.size(); i++) {
            Log.i("Category Name:", categoryNames.get(i));
            Log.i("Category Code:", categoryCodes.get(i).toString());
        }
    }

    private void loadSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(arrayAdapter);
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quizCategory = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }

        });
    }
}