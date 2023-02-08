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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.technophile.nuro.R;
import com.technophile.nuro.databinding.DialogFragmentEditDeckBinding;
import com.technophile.nuro.deck.DeckModel;
import com.technophile.nuro.utils.ImageHelper;

import java.io.FileNotFoundException;

public class EditDeckDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = EditDeckDialogFragment.class.getSimpleName();

    private DeckFragment deckFragment;
    private boolean demoMode;
    private ActivityResultLauncher<String> mGetContent;
    private DeckModel deckModel;
    private byte[] image = null;
    private int adapterPosition = -1;

    private DialogFragmentEditDeckBinding binding;

    public EditDeckDialogFragment() {

    }

    public EditDeckDialogFragment(boolean demoMode) {
        this.demoMode = demoMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (deckModel == null || adapterPosition == -1) {
            this.dismiss();
        }

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Theme_App_BottomSheet_Modal);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentEditDeckBinding.inflate(inflater, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("demoMode")) {
            demoMode = savedInstanceState.getBoolean("demoMode");
        }

        deckFragment = (DeckFragment) getParentFragment();
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

        binding.nameEditText.setText(deckModel.name);
        if (deckModel.description != null) {
            binding.descriptionEditText.setText(deckModel.description);
        }
        binding.imageView.setImageBitmap(ImageHelper.toBitmap(deckModel.image));

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

        binding.editDeckButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;
            EditText descriptionEditText = binding.descriptionEditText;

            if (image != null && !nameEditText.getText().toString().isEmpty()) {
                long rowsAffected;
                if (demoMode) {
                    rowsAffected = 1;
                }
                else {
                    rowsAffected = deckFragment.collectionTableManager.update(deckModel.id, deckModel.name, nameEditText.getText().toString(), image, descriptionEditText.getText().toString());
                }
                if (rowsAffected > 0) {
                    DeckModel newDeckModel = new DeckModel(deckModel.id, image, nameEditText.getText().toString(), descriptionEditText.getText().toString());
                    deckFragment.mAdapter.changeItem(adapterPosition, newDeckModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully edited the deck", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while editing the deck", Snackbar.LENGTH_LONG)
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

    public void setDeckModel(DeckModel deckModel) {
        this.deckModel = deckModel;
        this.image = deckModel.image;
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
