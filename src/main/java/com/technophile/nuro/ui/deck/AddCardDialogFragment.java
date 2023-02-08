package com.technophile.nuro.ui.deck;

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

import com.technophile.nuro.R;
import com.technophile.nuro.card.CardModel;
import com.technophile.nuro.databinding.DialogFragmentAddCardBinding;
import com.technophile.nuro.utils.ImageHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;

public class AddCardDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = AddCardDialogFragment.class.getSimpleName();

    private CardFragment cardFragment;
    private boolean demoMode;
    private ActivityResultLauncher<String> mGetContent;
    private byte[] image = null;

    private DialogFragmentAddCardBinding binding;

    public AddCardDialogFragment() {

    }

    public AddCardDialogFragment(boolean demoMode) {
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
        binding = DialogFragmentAddCardBinding.inflate(inflater, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("demoMode")) {
            demoMode = savedInstanceState.getBoolean("demoMode");
        }

        cardFragment = (CardFragment) getParentFragment();
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

        binding.selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.addCardButton.setOnClickListener(view1 -> {
            EditText captionEditText = binding.captionEditText;
            EditText shortAnswerEditText = binding.shortAnswerEditText;

            if (image != null && !captionEditText.getText().toString().isEmpty() && !shortAnswerEditText.getText().toString().isEmpty()) {
                long rowNumber;
                if (demoMode) {
                    rowNumber = cardFragment.mAdapter.getMaxId() + 1;
                }
                else {
                    rowNumber = cardFragment.deckTableManager.insert(image, captionEditText.getText().toString(), shortAnswerEditText.getText().toString());
                }
                if (rowNumber != -1) {
                    CardModel cardModel = new CardModel(rowNumber, image, captionEditText.getText().toString(), shortAnswerEditText.getText().toString());
                    cardFragment.mAdapter.addItem(cardModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully added the card", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {
                                }).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while adding the card", Snackbar.LENGTH_LONG)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
