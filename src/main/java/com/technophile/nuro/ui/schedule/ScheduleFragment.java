package com.technophile.nuro.ui.schedule;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technophile.nuro.R;
import com.technophile.nuro.common.MyArrayAdapter;
import com.technophile.nuro.databinding.FragmentScheduleBinding;
import com.technophile.nuro.schedule.ScheduleTableHelper;
import com.technophile.nuro.schedule.ScheduleTableManager;
import com.technophile.nuro.schedule.TaskAdapter;
import com.technophile.nuro.schedule.TaskDetailsLookup;
import com.technophile.nuro.schedule.TaskItemKeyProvider;
import com.technophile.nuro.schedule.TaskModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
    private BottomSheetDialog bottomSheetDialog;

    private boolean demoMode = false;

    private FragmentScheduleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add && getChildFragmentManager().getFragments().isEmpty()) {
                AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment(demoMode);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                addTaskDialogFragment.show(transaction, AddTaskDialogFragment.TAG);
                return true;
            }
            else if (item.getItemId() == R.id.action_help) {
                binding.chooseDayAutoCompleteTextView.setText(getString(R.string.introduction_text_fragment_schedule), false);
                demoMode = true;
                daySelected("Introduction");
                return true;
            }
            return false;
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        mRecyclerView = binding.recyclerView;
        if (dpWidth > 600) {
            mLayoutManager = new GridLayoutManager(getContext(), (int) (dpWidth / 400));
        }
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
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
                    bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_dialog);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView editTextView = bottomSheetDialog.findViewById(R.id.action_edit);
                    if (editTextView != null) {
                        editTextView.setOnClickListener(view1 -> {
                            bottomSheetDialog.dismiss();
                            if (!selectionTracker.hasSelection()) {
                                return;
                            }
                            long id = selectionTracker.getSelection().iterator().next();
                            selectionTracker.clearSelection();
                            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(id);
                            if (viewHolder != null) {
                                TaskModel taskModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment(demoMode);
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
                        deleteTextView.setOnClickListener(view1 -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete task?")
                                    .setMessage("The selected task will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            if (!demoMode) {
                                                scheduleTableManager.deleteRow(id);
                                            }
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Snackbar.make(view, "Deleted the task", Snackbar.LENGTH_LONG)
                                                    .setAction("OKAY", view2 -> {}).show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                    .setOnCancelListener(dialogInterface -> selectionTracker.clearSelection())
                                    .show();
                        });
                    }

                    MaterialTextView cancelTextView = bottomSheetDialog.findViewById(R.id.action_cancel);
                    if (cancelTextView != null) {
                        cancelTextView.setOnClickListener(view1 -> bottomSheetDialog.cancel());
                    }

                    bottomSheetDialog.show();
                }
            }
        });
        mAdapter.setSelectionTracker(selectionTracker);

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (binding.markAllAsPendingButton.getVisibility() != View.VISIBLE) {
                    binding.markAllAsPendingButton.setVisibility(View.VISIBLE);
                }
                if (binding.emptyDayTextView.getVisibility() != View.GONE) {
                    binding.emptyDayTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() == 0) {
                    binding.markAllAsPendingButton.setVisibility(View.GONE);
                    binding.emptyDayTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        mAdapter.registerAdapterDataObserver(adapterDataObserver);

        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, days);
        binding.chooseDayAutoCompleteTextView.setAdapter(adapter);

        binding.chooseDayAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            demoMode = false;
            daySelected(adapterView.getItemAtPosition(i).toString());
        });

        scheduleTableManager = new ScheduleTableManager(getContext());
        mAdapter.setScheduleTableManager(scheduleTableManager);

        binding.markAllAsPendingButton.setOnClickListener(view1 -> {
            scheduleTableManager.markAllAsPending();
            daySelected(binding.chooseDayAutoCompleteTextView.getText().toString());
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!binding.chooseDayAutoCompleteTextView.getText().toString().isEmpty()) {
            daySelected(binding.chooseDayAutoCompleteTextView.getText().toString());
        }
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            String day = formatter.format(Calendar.getInstance().getTime());
            binding.chooseDayAutoCompleteTextView.setText(day, false);
            daySelected(day);
        }
    }

    private void daySelected(String day) {
        mAdapter.clearAll();

        scheduleTableManager.close();
        scheduleTableManager.open(day);
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
            TaskModel taskModel = new TaskModel(cursor.getLong(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex), cursor.getString(instructionIndex), cursor.getLong(startTimeIndex), cursor.getLong(durationIndex), cursor.getInt(completedIndex) > 0, cursor.getLong(currentEndTimeIndex));
            mAdapter.addItem(taskModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        scheduleTableManager.close();
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        }
    }
}