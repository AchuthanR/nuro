package com.capstone.autism_training.card;

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

public class AddCardDialogFragment extends DialogFragment {

    public static final String TAG = "AddCardDialog";

    private String TABLE_NAME = "";
    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;

    public AddCardDialogFragment(String table_name) {
        TABLE_NAME = table_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_LanguageTherapyAssistanceForAutisticChildren);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view1 -> this.dismiss());

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null) {
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

        Button addCardButton = view.findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(view1 -> {
            EditText captionEditText = view.findViewById(R.id.captionEditText);
            EditText answerEditText = view.findViewById(R.id.answerEditText);

            if (image != null && !captionEditText.getText().toString().equals("") && !answerEditText.getText().toString().equals("")) {
                DeckTableManager deckTableManager = new DeckTableManager(getContext());
                deckTableManager.open(TABLE_NAME);
                long rowNumber = deckTableManager.insert(image, captionEditText.getText().toString(), answerEditText.getText().toString());
                if (rowNumber != -1) {
                    Toast.makeText(getContext(), "Successfully added the deck", Toast.LENGTH_LONG).show();
                    deckTableManager.close();
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
