package com.capstone.autism_training.ui.deck;

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
import com.capstone.autism_training.databinding.DialogFragmentAddDeckBinding;
import com.capstone.autism_training.deck.DeckModel;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;

public class AddDeckDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = AddDeckDialogFragment.class.getSimpleName();

    private ActivityResultLauncher<String> mGetContent;
    private DeckFragment deckFragment;
    private byte[] image = null;

    private DialogFragmentAddDeckBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheet_Modal);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentAddDeckBinding.inflate(inflater, container, false);

        deckFragment = (DeckFragment) getParentFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null) {
            getDialog().getWindow().getAttributes().windowAnimations = com.google.android.material.R.style.Animation_Design_BottomSheetDialog;
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

        binding.selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.addDeckButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;
            EditText descriptionEditText = binding.descriptionEditText;

            if (image != null && !nameEditText.getText().toString().isEmpty() && !descriptionEditText.getText().toString().isEmpty()) {
                long rowNumber = deckFragment.deckInfoTableManager.insert(nameEditText.getText().toString(), image, descriptionEditText.getText().toString());
                if (rowNumber != -1) {
                    DeckModel deckModel = new DeckModel(rowNumber, image, nameEditText.getText().toString(), descriptionEditText.getText().toString());
                    deckFragment.mAdapter.addItem(deckModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully added the deck", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while adding the deck", Snackbar.LENGTH_LONG)
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
