package com.capstone.autism_training.deck;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.CardActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class DeckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddDeckDialogFragment addDeckDialogFragment = new AddDeckDialogFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addDeckDialogFragment.show(transaction, AddDeckDialogFragment.TAG);
        });

        MaterialCardView materialCardView = findViewById(R.id.cardView);
        materialCardView.setOnClickListener(view2 -> {
            Intent intent = new Intent(this, CardActivity.class);
            if (materialCardView.getTag() != null) {
                intent.putExtra("TABLE_NAME", materialCardView.getTag().toString());
            }
            startActivity(intent);
        });
    }
}