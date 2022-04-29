package com.capstone.autism_training.ui.schedule;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.databinding.FragmentEditTaskBinding;
import com.capstone.autism_training.schedule.TaskModel;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EditTaskDialogFragment extends DialogFragment {

    public static final String TAG = EditTaskDialogFragment.class.getSimpleName();

    public ScheduleFragment scheduleFragment;
    private ActivityResultLauncher<String> mGetContent;
    private TaskModel taskModel;
    private byte[] image = null;
    private long start_time = -1;
    private int adapterPosition = -1;

    private FragmentEditTaskBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTaskBinding.inflate(inflater, container, false);

        scheduleFragment = (ScheduleFragment) getParentFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(view1 -> this.dismiss());

        binding.nameEditText.setText(taskModel.name);
        binding.instructionEditText.setText(taskModel.instruction);
        DateFormat dateFormat1 = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        binding.startTimeTextView.setText(String.format("Selected start time: %1$s", dateFormat1.format(start_time)));
        long hours = TimeUnit.MILLISECONDS.toHours(taskModel.duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(taskModel.duration) - hours * 60;
        binding.durationHourEditText.setText(String.valueOf(hours));
        binding.durationMinuteEditText.setText(String.valueOf(minutes));
        binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(taskModel.image));

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null && uri != null) {
                            image = ImageHelper.getBitmapAsByteArray(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri)));
                            binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_LONG).show();
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

            if (image != null && start_time != -1 && !nameEditText.getText().toString().isEmpty() && !instructionEditText.getText().toString().isEmpty() && !durationHourEditText.getText().toString().isEmpty() && !durationMinuteEditText.getText().toString().isEmpty()) {
                try {
                    long hour = Long.parseLong(durationHourEditText.getText().toString());
                    long minute = Long.parseLong(durationMinuteEditText.getText().toString());
                    if (hour < 0 || hour > 23) {
                        Toast.makeText(getContext(), "Hour value should be between 0 and 23", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (minute < 0 || minute > 59) {
                        Toast.makeText(getContext(), "Minute value should be between 0 and 59", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                catch (NumberFormatException exception) {
                    Toast.makeText(getContext(), "Hour and minute values should be numbers!", Toast.LENGTH_LONG).show();
                    return;
                }

                long duration = TimeUnit.HOURS.toMillis(Long.parseLong(durationHourEditText.getText().toString())) + TimeUnit.MINUTES.toMillis(Long.parseLong(durationMinuteEditText.getText().toString()));
                long rowsAffected = scheduleFragment.scheduleTableManager.update(taskModel.id, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration);
                if (rowsAffected > 0) {
                    TaskModel newTaskModel = new TaskModel(taskModel.id, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration, taskModel.completed, taskModel.current_end_time);
                    scheduleFragment.mAdapter.changeItem(adapterPosition, newTaskModel);
                    Toast.makeText(getContext(), "Successfully edited the task", Toast.LENGTH_LONG).show();
                    this.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Error while editing the task", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getContext(), "All fields are necessary", Toast.LENGTH_LONG).show();
            }
        });
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
