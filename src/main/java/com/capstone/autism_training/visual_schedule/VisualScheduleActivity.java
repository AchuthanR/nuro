package com.capstone.autism_training.visual_schedule;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class VisualScheduleActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected TaskAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public VisualScheduleTableManager visualScheduleTableManager;
    private SelectionTracker<Long> selectionTracker;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_schedule);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addTaskDialogFragment.show(transaction, AddTaskDialogFragment.TAG);
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TaskAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "selectionId",
                mRecyclerView,
                new TaskItemKeyProvider(mRecyclerView),
                new TaskDetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
                .build();

        ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_task, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    new MaterialAlertDialogBuilder(VisualScheduleActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setIcon(R.drawable.ic_round_delete_24)
                            .setTitle("Delete task?")
                            .setMessage("The selected task will be deleted permanently.")
                            .setPositiveButton("Delete", (dialogInterface, i) -> {
                                if (selectionTracker.hasSelection()) {
                                    long id = selectionTracker.getSelection().iterator().next();
                                    selectionTracker.clearSelection();
                                    visualScheduleTableManager.deleteRow(id);
                                    mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                    Toast.makeText(VisualScheduleActivity.this, "Deleted the task", Toast.LENGTH_LONG).show();
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

        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, days);
        MaterialAutoCompleteTextView chooseDayAutoCompleteTextView = findViewById(R.id.chooseDayAutoCompleteTextView);
        chooseDayAutoCompleteTextView.setAdapter(adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String day = formatter.format(Calendar.getInstance().getTime());
        chooseDayAutoCompleteTextView.setText(day, false);
        chooseDayAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> daySelected(adapterView.getItemAtPosition(i).toString()));

        visualScheduleTableManager = new VisualScheduleTableManager(getApplicationContext());
        mAdapter.setVisualScheduleTableManager(visualScheduleTableManager);
        daySelected(day);

        MaterialButton markAllAsPending = findViewById(R.id.markAllAsPendingButton);
        markAllAsPending.setOnClickListener(view -> {
            visualScheduleTableManager.markAllAsPending();
            daySelected(chooseDayAutoCompleteTextView.getText().toString());
        });

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (markAllAsPending.getVisibility() != View.VISIBLE) {
                    markAllAsPending.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() == 0) {
                    markAllAsPending.setVisibility(View.GONE);
                }
            }
        };
        mAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void daySelected(String day) {
        mAdapter.clearAll();

        visualScheduleTableManager.close();
        visualScheduleTableManager.open(day.toUpperCase().replace(" ", "_"));
        Cursor cursor = visualScheduleTableManager.fetch();

        int idIndex = cursor.getColumnIndex(VisualScheduleTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(VisualScheduleTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(VisualScheduleTableHelper.IMAGE);
        int instructionIndex = cursor.getColumnIndex(VisualScheduleTableHelper.INSTRUCTION);
        int startTimeIndex = cursor.getColumnIndex(VisualScheduleTableHelper.START_TIME);
        int durationIndex = cursor.getColumnIndex(VisualScheduleTableHelper.DURATION);
        int completedIndex = cursor.getColumnIndex(VisualScheduleTableHelper.COMPLETED);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            TaskModel taskModel = new TaskModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex), cursor.getString(instructionIndex), cursor.getLong(startTimeIndex), cursor.getLong(durationIndex), cursor.getInt(completedIndex) > 0);
            mAdapter.addItem(taskModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visualScheduleTableManager.close();
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }
}