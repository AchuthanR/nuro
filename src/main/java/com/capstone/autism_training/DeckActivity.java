package com.capstone.autism_training;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.capstone.autism_training.databinding.ActivityDeckBinding;

public class DeckActivity extends AppCompatActivity {

    private ActivityDeckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //MaterialToolbar toolbar = binding.toolbar;
        //setSupportActionBar(toolbar);
    }
}