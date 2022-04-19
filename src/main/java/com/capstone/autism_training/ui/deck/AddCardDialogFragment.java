package com.capstone.autism_training.ui.deck;

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
import com.capstone.autism_training.card.CardModel;
import com.capstone.autism_training.databinding.FragmentAddCardBinding;
import com.capstone.autism_training.utilities.ImageHelper;

import java.io.FileNotFoundException;

public class AddCardDialogFragment extends DialogFragment {

    public static final String TAG = AddCardDialogFragment.class.getSimpleName();

    private ActivityResultLauncher<String> mGetContent;
    private CardFragment cardFragment;
    private byte[] image = null;

    private FragmentAddCardBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCardBinding.inflate(inflater, container, false);

        cardFragment = (CardFragment) getParentFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(view1 -> this.dismiss());

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

        binding.selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.addCardButton.setOnClickListener(view1 -> {
            EditText captionEditText = binding.captionEditText;
            EditText answerEditText = binding.answerEditText;

            if (image != null && !captionEditText.getText().toString().isEmpty() && !answerEditText.getText().toString().isEmpty()) {
                long rowNumber = cardFragment.deckTableManager.insert(image, captionEditText.getText().toString(), answerEditText.getText().toString());
                if (rowNumber != -1) {
                    CardModel cardModel = new CardModel(rowNumber, image, captionEditText.getText().toString(), answerEditText.getText().toString());
                    cardFragment.mAdapter.addItem(cardModel);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
