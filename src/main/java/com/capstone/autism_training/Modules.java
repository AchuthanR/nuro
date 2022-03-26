package com.capstone.autism_training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Modules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
    }

    public void showDecks_onClick(View view) {
        startActivity(new Intent(this, DeckActivity.class));
    }
}

