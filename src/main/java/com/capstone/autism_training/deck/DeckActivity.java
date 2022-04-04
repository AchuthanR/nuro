package com.capstone.autism_training.deck;

import android.database.Cursor;
import android.os.Bundle;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeckActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected DeckAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

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

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DeckAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getApplicationContext());
        deckInfoTableManager.open();
        Cursor cursor = deckInfoTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckInfoTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckInfoTableHelper.IMAGE);
        int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
        int descriptionIndex = cursor.getColumnIndex(DeckInfoTableHelper.DESCRIPTION);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            DeckModel deckModel = new DeckModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex));
            mAdapter.addItem(deckModel);
            cursor.moveToNext();
        }
        deckInfoTableManager.close();
        cursor.close();
    }
}