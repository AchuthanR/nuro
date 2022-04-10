package com.capstone.autism_training.visual_schedule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AddTaskDialogFragment extends DialogFragment {

    public static final String TAG = "AddTaskDialog";
    public static String TABLE_NAME = "";

    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;
    private long start_time = -1;

    public AddTaskDialogFragment(String table_name) {
        TABLE_NAME = table_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_task, container, false);
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
                            image = getBitmapAsByteArray(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri)));
                            BitmapFactory.Options options1 = new BitmapFactory.Options();
                            options1.inJustDecodeBounds = true;
                            BitmapFactory.decodeByteArray(image, 0, image.length, options1);

                            final int REQUIRED_SIZE = 300;

                            int width_tmp = options1.outWidth, height_tmp = options1.outHeight;
                            int scale = 1;
                            while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
                                width_tmp /= 2;
                                height_tmp /= 2;
                                scale *= 2;
                            }

                            BitmapFactory.Options options2 = new BitmapFactory.Options();
                            options2.inSampleSize = scale;
                            options2.inJustDecodeBounds = false;
                            ImageView imageView = view.findViewById(R.id.imageView);
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length, options2));
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });

        Button selectStartTimeButton = view.findViewById(R.id.selectStartTimeButton);
        selectStartTimeButton.setOnClickListener(view1 -> {
            int hour = 12;
            int minute = 0;
            if (start_time != -1) {
                hour = (int) TimeUnit.MILLISECONDS.toHours(start_time);
                minute = (int) TimeUnit.MILLISECONDS.toMinutes(start_time);
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

        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        Button addTaskButton = view.findViewById(R.id.addTaskButton);
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

                VisualScheduleTableManager visualScheduleTableManager = new VisualScheduleTableManager(getContext());
                visualScheduleTableManager.open(TABLE_NAME);
                long duration = TimeUnit.HOURS.toMillis(Long.parseLong(durationHourEditText.getText().toString())) + TimeUnit.MINUTES.toMillis(Long.parseLong(durationMinuteEditText.getText().toString()));
                long rowNumber = visualScheduleTableManager.insert(nameEditText.getText().toString(), image, instructionEditText.getText().toString(), start_time, duration);
                if (rowNumber != -1) {
                    Toast.makeText(getContext(), "Successfully added the task", Toast.LENGTH_LONG).show();
                    visualScheduleTableManager.close();
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

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
