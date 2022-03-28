package com.capstone.autism_training;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.autism_training.deck.DeckActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        MaterialCardView materialCardView = findViewById(R.id.allDecksCardView);
        materialCardView.setOnClickListener(view -> startActivity(new Intent(this, DeckActivity.class)) );

        //for starting the identification task
        MaterialCardView materialCardView1 = findViewById(R.id.identificationTask);
        materialCardView1.setOnClickListener(view -> startActivity(new Intent(this, IdentificationQuestionSelection.class)) );
    }
}