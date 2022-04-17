package com.capstone.autism_training.card;

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

import java.io.FileNotFoundException;

public class AddCardDialogFragment extends DialogFragment {

    public static final String TAG = "AddCardDialog";

    private ActivityResultLauncher<String> mGetContent;
    private CardActivity cardActivity;
    private byte[] image = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cardActivity = (CardActivity) getActivity();
        return inflater.inflate(R.layout.fragment_add_card, container, false);
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

        MaterialButton selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        MaterialButton addCardButton = view.findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(view1 -> {
            EditText captionEditText = view.findViewById(R.id.captionEditText);
            EditText answerEditText = view.findViewById(R.id.answerEditText);

            if (image != null && !captionEditText.getText().toString().equals("") && !answerEditText.getText().toString().equals("")) {
                long rowNumber = cardActivity.deckTableManager.insert(image, captionEditText.getText().toString(), answerEditText.getText().toString());
                if (rowNumber != -1) {
                    CardModel cardModel = new CardModel(rowNumber, image, captionEditText.getText().toString(), answerEditText.getText().toString());
                    cardActivity.mAdapter.addItem(cardModel);
                    Toast.makeText(getContext(), "Successfully added the deck", Toast.LENGTH_LONG).show();
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
}
