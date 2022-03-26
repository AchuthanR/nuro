package com.capstone.autism_training.card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.capstone.autism_training.R;
import com.capstone.autism_training.deck.AddDeckDialogFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddCardDialogFragment addCardDialogFragment = new AddCardDialogFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addCardDialogFragment.show(transaction, AddCardDialogFragment.TAG);
        });
    }
}