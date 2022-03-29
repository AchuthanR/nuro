package com.capstone.autism_training.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.capstone.autism_training.R;

public class IdentificationQuestionSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_question_selection);
    }

    public void goto_identification_activity(View view) {
        startActivity(new Intent(this, IdentificationActivity.class));
    }
}