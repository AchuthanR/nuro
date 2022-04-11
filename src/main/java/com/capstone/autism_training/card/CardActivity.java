package com.capstone.autism_training.card;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CardActivity extends AppCompatActivity {

    private String TABLE_NAME = "";

    protected RecyclerView mRecyclerView;
    protected CardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SelectionTracker<Long> selectionTracker;
    private DeckTableManager deckTableManager;
    private ActionMode actionMode;

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

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CardAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "selectionId",
                mRecyclerView,
                new CardItemKeyProvider(mRecyclerView),
                new CardDetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
                .build();

        ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_card, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    new MaterialAlertDialogBuilder(CardActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setIcon(R.drawable.ic_round_delete_24)
                            .setTitle("Delete card?")
                            .setMessage("The selected card will be deleted permanently.")
                            .setPositiveButton("Delete", (dialogInterface, i) -> {
                                if (selectionTracker.hasSelection()) {
                                    long id = selectionTracker.getSelection().iterator().next();
                                    selectionTracker.clearSelection();
                                    deckTableManager.deleteRow(id);
                                    mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                    Toast.makeText(CardActivity.this, "Deleted the card", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                            .setOnDismissListener(dialogInterface -> mode.finish())
                            .show();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                selectionTracker.clearSelection();
            }
        };

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (!selectionTracker.getSelection().isEmpty() && actionMode == null) {
                    actionMode = toolbar.startActionMode(actionModeCallback);
                }
                else if (selectionTracker.getSelection().isEmpty() && actionMode != null) {
                    actionMode.finish();
                }
            }
        });
        mAdapter.setSelectionTracker(selectionTracker);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        deckTableManager = new DeckTableManager(getApplicationContext());
        deckTableManager.open(TABLE_NAME);
        Cursor cursor = deckTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int answerIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(answerIndex));
            mAdapter.addItem(cardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deckTableManager.close();
    }
}