package com.capstone.autism_training.card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CardActivity extends AppCompatActivity {

    private String TABLE_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (getIntent().hasExtra("TABLE_NAME")) {
            TABLE_NAME = getIntent().getStringExtra("TABLE_NAME");
            toolbar.setTitle("Flash cards in " + TABLE_NAME);
        }

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddCardDialogFragment addCardDialogFragment = new AddCardDialogFragment(TABLE_NAME);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addCardDialogFragment.show(transaction, AddCardDialogFragment.TAG);
        });
    }
}