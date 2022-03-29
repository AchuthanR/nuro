package com.capstone.autism_training.card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    private String TABLE_NAME = "";

    protected RecyclerView mRecyclerView;
    protected CardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<CardModel> mCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (getIntent().hasExtra("TABLE_NAME")) {
            TABLE_NAME = getIntent().getStringExtra("TABLE_NAME").replace(" ", "_");
            toolbar.setTitle(getIntent().getStringExtra("TABLE_NAME") + " deck");
        }

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddCardDialogFragment addCardDialogFragment = new AddCardDialogFragment(TABLE_NAME);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addCardDialogFragment.show(transaction, AddCardDialogFragment.TAG);
        });

        mCards = new ArrayList<>();
        initializeDecks();

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter(mCards);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initializeDecks() {
        DeckTableManager deckTableManager = new DeckTableManager(getApplicationContext());
        deckTableManager.open(TABLE_NAME);
        Cursor cursor = deckTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int answerIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(answerIndex));
            mCards.add(cardModel);
            cursor.moveToNext();
        }
        deckTableManager.close();
        cursor.close();
    }
}