package com.capstone.autism_training.deck;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class AddDeckDialogFragment extends DialogFragment {

    public static final String TAG = "AddDeckDialog";

    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_deck, container, false);
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
                        Toast.makeText(getContext(), "File not found!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });

        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(view1 -> {
            mGetContent.launch("image/*");
        });

        Button addDeckButton = view.findViewById(R.id.addDeckButton);
        addDeckButton.setOnClickListener(view1 -> {
            EditText nameEditText = view.findViewById(R.id.nameEditText);
            EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);

            if (image != null && !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("")) {
                DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getContext());
                deckInfoTableManager.open();
                long rowNumber = deckInfoTableManager.insert(nameEditText.getText().toString(), image, descriptionEditText.getText().toString());
                if (rowNumber != -1) {
                    Toast.makeText(getContext(), "Successfully added the deck", Toast.LENGTH_LONG).show();
                    deckInfoTableManager.close();
                    this.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Error while adding the deck", Toast.LENGTH_LONG).show();
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
