package com.capstone.autism_training.visual_schedule;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AddTaskDialogFragment extends DialogFragment {

    public static final String TAG = "AddTaskDialog";

    public VisualScheduleActivity visualScheduleActivity;
    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;
    private long start_time = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        visualScheduleActivity = (VisualScheduleActivity) getActivity();
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view1 -> this.dismiss());

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null && uri != null) {
                            image = ImageHelper.getBitmapAsByteArray(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri)));
                            ImageView imageView = view.findViewById(R.id.imageView);
                            imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });

        MaterialButton selectStartTimeButton = view.findViewById(R.id.selectStartTimeButton);
        selectStartTimeButton.setOnClickListener(view1 -> {
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
                    .setTitleText("Select start time")
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText("Cancel")
                    .build();

            timePicker.addOnPositiveButtonClickListener(view2 -> {
                start_time = TimeUnit.HOURS.toMillis(timePicker.getHour()) + TimeUnit.MINUTES.toMillis(timePicker.getMinute());
                if (getView() != null) {
                    MaterialTextView textView = getView().findViewById(R.id.startTimeTextView);
                    DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    textView.setText(String.format("Selected start time: %1$s", dateFormat.format(start_time)));
                }
            });

            timePicker.show(getChildFragmentManager(), "timePicker");
        });

        MaterialButton selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        MaterialButton addTaskButton = view.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(view1 -> {
            EditText nameEditText = view.findViewById(R.id.nameEditText);
            EditText instructionEditText = view.findViewById(R.id.instructionEditText);
            EditText durationHourEditText = view.findViewById(R.id.durationHourEditText);
            EditText durationMinuteEditText = view.findViewById(R.id.durationMinuteEditText);

            if (image != null && start_time != -1 && !nameEditText.getText().toString().equals("") && !instructionEditText.getText().toString().equals("") && !durationHourEditText.getText().toString().equals("") && !durationMinuteEditText.getText().toString().equals("")) {
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
                long rowNumber = visualScheduleActivity.visualScheduleTableManager.insert(nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration);
                if (rowNumber != -1) {
                    TaskModel taskModel = new TaskModel(rowNumber, nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration, false, -1);
                    visualScheduleActivity.mAdapter.addItemAtRightPosition(taskModel);
                    Toast.makeText(getContext(), "Successfully added the task", Toast.LENGTH_LONG).show();
                    this.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Error while adding the task", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getContext(), "All fields are necessary", Toast.LENGTH_LONG).show();
            }
        });
    }
}
