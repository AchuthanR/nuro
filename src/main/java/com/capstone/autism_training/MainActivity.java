package com.capstone.autism_training;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.autism_training.activities.ActivitiesActivity;
import com.capstone.autism_training.deck.DeckActivity;
import com.capstone.autism_training.help.HelpCardActivity;
import com.capstone.autism_training.training.TrainingActivity;
import com.capstone.autism_training.visual_schedule.VisualScheduleActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView materialCardView = findViewById(R.id.allDecksCardView);
        materialCardView.setOnClickListener(view -> startActivity(new Intent(this, DeckActivity.class)) );

        MaterialCardView materialCardView1 = findViewById(R.id.visualScheduleCardView);
        materialCardView1.setOnClickListener(view -> startActivity(new Intent(this, VisualScheduleActivity.class)) );

        MaterialCardView materialCardView2 = findViewById(R.id.activitiesCardView);
        materialCardView2.setOnClickListener(view -> startActivity(new Intent(this, ActivitiesActivity.class)) );

        MaterialCardView materialCardView3 = findViewById(R.id.helpCardCardView);
        materialCardView3.setOnClickListener(view -> startActivity(new Intent(this, HelpCardActivity.class)) );

        MaterialCardView materialCardView4 = findViewById(R.id.trainingCardView);
        materialCardView4.setOnClickListener(view -> startActivity(new Intent(this, TrainingActivity.class)) );
    }
}