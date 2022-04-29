package com.capstone.autism_training.ui.schedule;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.common.MyArrayAdapter;
import com.capstone.autism_training.databinding.FragmentScheduleBinding;
import com.capstone.autism_training.schedule.ScheduleTableHelper;
import com.capstone.autism_training.schedule.ScheduleTableManager;
import com.capstone.autism_training.schedule.TaskAdapter;
import com.capstone.autism_training.schedule.TaskDetailsLookup;
import com.capstone.autism_training.schedule.TaskItemKeyProvider;
import com.capstone.autism_training.schedule.TaskModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    public static final String TAG = ScheduleFragment.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    protected TaskAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public ScheduleTableManager scheduleTableManager;
    private SelectionTracker<Long> selectionTracker;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    private FragmentScheduleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.extendedFAB.setOnClickListener(view1 -> {
            AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addTaskDialogFragment.show(transaction, AddTaskDialogFragment.TAG);
        });

        mRecyclerView = binding.recyclerView;
        mLayoutManager = new LinearLayoutManager(getContext());
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

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (!selectionTracker.getSelection().isEmpty() && getContext() != null) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet_dialog);
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView editTextView = bottomSheetDialog.findViewById(R.id.action_edit);
                    if (editTextView != null) {
                        editTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            if (!selectionTracker.hasSelection()) {
                                return;
                            }
                            long id = selectionTracker.getSelection().iterator().next();
                            selectionTracker.clearSelection();
                            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(id);
                            if (viewHolder != null) {
                                TaskModel taskModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();
                                editTaskDialogFragment.setTaskModel(taskModel);
                                editTaskDialogFragment.setAdapterPosition(viewHolder.getAdapterPosition());

                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                editTaskDialogFragment.show(transaction, EditTaskDialogFragment.TAG);
                            }
                        });
                    }

                    MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                    if (deleteTextView != null) {
                        deleteTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete task?")
                                    .setMessage("The selected task will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            scheduleTableManager.deleteRow(id);
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Toast.makeText(getContext(), "Deleted the task", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                    .setOnCancelListener(dialogInterface -> selectionTracker.clearSelection())
                                    .show();
                        });
                    }

                    MaterialTextView cancelTextView = bottomSheetDialog.findViewById(R.id.action_cancel);
                    if (cancelTextView != null) {
                        cancelTextView.setOnClickListener(view -> bottomSheetDialog.cancel());
                    }

                    bottomSheetDialog.show();
                }
            }
        });
        mAdapter.setSelectionTracker(selectionTracker);

        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, days);
        binding.chooseDayAutoCompleteTextView.setAdapter(adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String day = formatter.format(Calendar.getInstance().getTime());
        binding.chooseDayAutoCompleteTextView.setText(day, false);
        binding.chooseDayAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> daySelected(adapterView.getItemAtPosition(i).toString()));

        scheduleTableManager = new ScheduleTableManager(getContext());
        mAdapter.setVisualScheduleTableManager(scheduleTableManager);

        binding.markAllAsPendingButton.setOnClickListener(view1 -> {
            scheduleTableManager.markAllAsPending();
            daySelected(binding.chooseDayAutoCompleteTextView.getText().toString());
        });

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (binding.markAllAsPendingButton.getVisibility() != View.VISIBLE) {
                    binding.markAllAsPendingButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() == 0) {
                    binding.markAllAsPendingButton.setVisibility(View.GONE);
                }
            }
        };
        mAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!binding.chooseDayAutoCompleteTextView.getText().toString().isEmpty()) {
            daySelected(binding.chooseDayAutoCompleteTextView.getText().toString());
        }
    }

    private void daySelected(String day) {
        mAdapter.clearAll();

        scheduleTableManager.close();
        scheduleTableManager.open(day.toUpperCase().replace(" ", "_"));
        Cursor cursor = scheduleTableManager.fetch();

        int idIndex = cursor.getColumnIndex(ScheduleTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(ScheduleTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(ScheduleTableHelper.IMAGE);
        int instructionIndex = cursor.getColumnIndex(ScheduleTableHelper.INSTRUCTION);
        int startTimeIndex = cursor.getColumnIndex(ScheduleTableHelper.START_TIME);
        int durationIndex = cursor.getColumnIndex(ScheduleTableHelper.DURATION);
        int completedIndex = cursor.getColumnIndex(ScheduleTableHelper.COMPLETED);
        int currentEndTimeIndex = cursor.getColumnIndex(ScheduleTableHelper.CURRENT_END_TIME);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            TaskModel taskModel = new TaskModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex), cursor.getString(instructionIndex), cursor.getLong(startTimeIndex), cursor.getLong(durationIndex), cursor.getInt(completedIndex) > 0, cursor.getLong(currentEndTimeIndex));
            mAdapter.addItem(taskModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }
}