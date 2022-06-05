package com.capstone.autism_training.ui.schedule;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.autism_training.R;
import com.capstone.autism_training.databinding.DialogFragmentAddTaskBinding;
import com.capstone.autism_training.schedule.TaskModel;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AddTaskDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = AddTaskDialogFragment.class.getSimpleName();

    public ScheduleFragment scheduleFragment;
    private final boolean demoMode;
    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;
    private long start_time = -1;

    private DialogFragmentAddTaskBinding binding;

    public AddTaskDialogFragment(boolean demoMode) {
        this.demoMode = demoMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheet_Modal);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentAddTaskBinding.inflate(inflater, container, false);

        scheduleFragment = (ScheduleFragment) getParentFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null) {
            getDialog().getWindow().getAttributes().windowAnimations = com.google.android.material.R.style.Animation_Design_BottomSheetDialog;
        }

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null && uri != null) {
                            image = ImageHelper.getBitmapAsByteArray(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri)));
                            binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));
                        }
                    } catch (FileNotFoundException e) {
                        Snackbar.make(view, "Image not found!", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view1 -> {}).show();
                        e.printStackTrace();
                    }
                });

        binding.selectStartTimeButton.setOnClickListener(view1 -> {
            int hour = 12;
            int minute = 0;
            if (start_time != -1) {
                hour = (int) TimeUnit.MILLISECONDS.toHours(start_time);
                minute = (int) TimeUnit.MILLISECONDS.toMinutes(start_time) - hour * 60;
            }
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(hour)
                    .setMinute(minute)
                    .setTitleText("SELECT START TIME")
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText("Cancel")
                    .build();

            timePicker.addOnPositiveButtonClickListener(view2 -> {
                start_time = TimeUnit.HOURS.toMillis(timePicker.getHour()) + TimeUnit.MINUTES.toMillis(timePicker.getMinute());
                DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                binding.startTimeTextView.setText(String.format("Selected start time: %1$s", dateFormat.format(start_time)));
            });

            timePicker.show(getChildFragmentManager(), "timePicker");
        });

        binding.selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.addTaskButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;
            EditText instructionEditText = binding.instructionEditText;
            EditText durationHourEditText = binding.durationHourEditText;
            EditText durationMinuteEditText = binding.durationMinuteEditText;

            if (image != null && start_time != -1 && !nameEditText.getText().toString().isEmpty() && !instructionEditText.getText().toString().isEmpty() && !durationHourEditText.getText().toString().isEmpty() && !durationMinuteEditText.getText().toString().isEmpty()) {
                long hour;
                long minute;
                try {
                    hour = Long.parseLong(durationHourEditText.getText().toString());
                    minute = Long.parseLong(durationMinuteEditText.getText().toString());
                    if (hour < 0 || hour > 23) {
                        Snackbar.make(view, "Hour value should be between 0 and 23", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                        return;
                    }
                    if (minute < 0 || minute > 59) {
                        Snackbar.make(view, "Minute value should be between 0 and 59", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                        return;
                    }
                }
                catch (NumberFormatException exception) {
                    Snackbar.make(view, "Hour and minute values should be numbers!", Snackbar.LENGTH_LONG)
                            .setAction("OKAY", view2 -> {}).show();
                    return;
                }

                long duration = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
                long rowNumber;
                if (demoMode) {
                    rowNumber = scheduleFragment.mAdapter.getMaxId() + 1;
                }
                else {
                    rowNumber = scheduleFragment.scheduleTableManager.insert(nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration);
                }
                if (rowNumber != -1) {
                    TaskModel taskModel = new TaskModel(rowNumber, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration, false, -1);
                    scheduleFragment.mAdapter.addItemAtRightPosition(taskModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully added the task", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while adding the task", Snackbar.LENGTH_LONG)
                            .setAction("OKAY", view2 -> {}).show();
                }
            }
            else {
                Snackbar.make(view, "All fields are necessary", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
