package com.capstone.autism_training.visual_schedule;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
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

    private MaterialAutoCompleteTextView chooseDayAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_schedule);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ExtendedFloatingActionButton extendedFAB = findViewById(R.id.extendedFAB);
        extendedFAB.setOnClickListener(view -> {
            AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment(chooseDayAutoCompleteTextView.getText().toString().toUpperCase());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addTaskDialogFragment.show(transaction, AddTaskDialogFragment.TAG);
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TaskAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, days);
        chooseDayAutoCompleteTextView = findViewById(R.id.chooseDayAutoCompleteTextView);
        chooseDayAutoCompleteTextView.setAdapter(adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String day = formatter.format(Calendar.getInstance().getTime());
        chooseDayAutoCompleteTextView.setText(day, false);
        chooseDayAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> daySelected(adapterView.getItemAtPosition(i).toString()));

        daySelected(day);
    }

    private void daySelected(String day) {
        mAdapter.clearAll();

        VisualScheduleTableManager visualScheduleTableManager = new VisualScheduleTableManager(getApplicationContext());
        visualScheduleTableManager.open(day.toUpperCase());
        Cursor cursor = visualScheduleTableManager.fetch();

        int idIndex = cursor.getColumnIndex(VisualScheduleTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(VisualScheduleTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(VisualScheduleTableHelper.IMAGE);
        int instructionIndex = cursor.getColumnIndex(VisualScheduleTableHelper.INSTRUCTION);
        int startTimeIndex = cursor.getColumnIndex(VisualScheduleTableHelper.START_TIME);
        int durationIndex = cursor.getColumnIndex(VisualScheduleTableHelper.DURATION);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            TaskModel taskModel = new TaskModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex), cursor.getString(instructionIndex), cursor.getLong(startTimeIndex), cursor.getLong(durationIndex));
            mAdapter.addItem(taskModel);
            cursor.moveToNext();
        }
        visualScheduleTableManager.close();
        cursor.close();
    }
}