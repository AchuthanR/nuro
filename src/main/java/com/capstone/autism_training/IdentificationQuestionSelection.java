package com.capstone.autism_training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class IdentificationQuestionSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_question_selection);
    }

    //from selection of question to the main identification task
    public void goto_identification_activity(View view) {
        startActivity(new Intent(this, IdentificationActivity.class));
    }
}