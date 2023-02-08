package com.technophile.nuro.ui.schedule;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.technophile.nuro.R;
import com.technophile.nuro.databinding.DialogFragmentEditTaskBinding;
import com.technophile.nuro.schedule.TaskModel;
import com.technophile.nuro.utils.ImageHelper;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EditTaskDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = EditTaskDialogFragment.class.getSimpleName();

    public ScheduleFragment scheduleFragment;
    private boolean demoMode;
    private ActivityResultLauncher<String> mGetContent;
    private TaskModel taskModel;
    private byte[] image = null;
    private long start_time = -1;
    private int adapterPosition = -1;

    private DialogFragmentEditTaskBinding binding;

    public EditTaskDialogFragment() {

    }

    public EditTaskDialogFragment(boolean demoMode) {
        this.demoMode = demoMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (taskModel == null || adapterPosition == -1) {
            this.dismiss();
        }

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheet_Modal);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentEditTaskBinding.inflate(inflater, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("demoMode")) {
            demoMode = savedInstanceState.getBoolean("demoMode");
        }

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
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        binding.nameEditText.setText(taskModel.name);
        if (taskModel.instruction != null) {
            binding.instructionEditText.setText(taskModel.instruction);
        }
        DateFormat dateFormat1 = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        binding.startTimeTextView.setText(String.format("Selected start time: %1$s", dateFormat1.format(taskModel.start_time)));
        long hours = TimeUnit.MILLISECONDS.toHours(taskModel.duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(taskModel.duration) - hours * 60;
        binding.durationHourEditText.setText(String.valueOf(hours));
        binding.durationMinuteEditText.setText(String.valueOf(minutes));
        binding.imageView.setImageBitmap(ImageHelper.toBitmap(taskModel.image));

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null && uri != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
                            if (bitmap == null) {
                                Snackbar.make(view, "Could not process the image", Snackbar.LENGTH_LONG)
                                        .setAction("OKAY", view1 -> {}).show();
                                return ;
                            }
                            Bitmap compressedBitmap = ImageHelper.compress(bitmap);
                            binding.imageView.setImageBitmap(compressedBitmap);
                            image = ImageHelper.toByteArray(compressedBitmap);
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

        binding.editTaskButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;
            EditText instructionEditText = binding.instructionEditText;
            EditText durationHourEditText = binding.durationHourEditText;
            EditText durationMinuteEditText = binding.durationMinuteEditText;

            if (image != null && start_time != -1 && !nameEditText.getText().toString().isEmpty() && !durationHourEditText.getText().toString().isEmpty() && !durationMinuteEditText.getText().toString().isEmpty()) {
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
                long rowsAffected;
                if (demoMode) {
                    rowsAffected = 1;
                }
                else {
                    rowsAffected = scheduleFragment.scheduleTableManager.update(taskModel.id, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration);
                }
                if (rowsAffected > 0) {
                    TaskModel newTaskModel = new TaskModel(taskModel.id, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration, taskModel.completed, taskModel.current_end_time);
                    scheduleFragment.mAdapter.changeItem(adapterPosition, newTaskModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully edited the task", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while editing the task", Snackbar.LENGTH_LONG)
                            .setAction("OKAY", view2 -> {}).show();
                }
            }
            else {
                Snackbar.make(view, "Please fill all the mandatory fields", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
            }

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("demoMode", demoMode);
    }

    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
        this.image = taskModel.image;
        this.start_time = taskModel.start_time;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
