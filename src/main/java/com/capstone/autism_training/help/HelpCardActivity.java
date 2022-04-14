package com.capstone.autism_training.help;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpCardActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected HelpCardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public HelpCardTableManager helpCardTableManager;
    private SelectionTracker<Long> selectionTracker;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_card);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddHelpCardDialogFragment addHelpCardDialogFragment = new AddHelpCardDialogFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addHelpCardDialogFragment.show(transaction, AddHelpCardDialogFragment.TAG);
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new HelpCardAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "selectionId",
                mRecyclerView,
                new HelpCardItemKeyProvider(mRecyclerView),
                new HelpCardDetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
                .build();

        ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_help_card, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    new MaterialAlertDialogBuilder(HelpCardActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setIcon(R.drawable.ic_round_delete_24)
                            .setTitle("Delete help card?")
                            .setMessage("The selected help card will be deleted permanently.")
                            .setPositiveButton("Delete", (dialogInterface, i) -> {
                                if (selectionTracker.hasSelection()) {
                                    long id = selectionTracker.getSelection().iterator().next();
                                    selectionTracker.clearSelection();
                                    helpCardTableManager.deleteRow(id);
                                    mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                    Toast.makeText(HelpCardActivity.this, "Deleted the help card", Toast.LENGTH_LONG).show();
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

        ArrayList<String> decks = new ArrayList<>(Arrays.asList("Requests", "Responses", "Emotions", "Problems"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, decks);
        MaterialAutoCompleteTextView chooseCategoryAutoCompleteTextView = findViewById(R.id.chooseCategoryAutoCompleteTextView);
        chooseCategoryAutoCompleteTextView.setAdapter(adapter);

        chooseCategoryAutoCompleteTextView.setText(decks.get(0), false);
        chooseCategoryAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> categorySelected(adapterView.getItemAtPosition(i).toString()));

        categorySelected(decks.get(0));
    }

    private void categorySelected(String deck) {
        mAdapter.clearAll();

        helpCardTableManager = new HelpCardTableManager(getApplicationContext());
        helpCardTableManager.open(deck.toUpperCase().replace(" ", "_"));
        Cursor cursor = helpCardTableManager.fetch();

        int idIndex = cursor.getColumnIndex(HelpCardTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(HelpCardTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(HelpCardTableHelper.IMAGE);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            HelpCardModel helpCardModel = new HelpCardModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex));
            mAdapter.addItem(helpCardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helpCardTableManager.close();
    }
}