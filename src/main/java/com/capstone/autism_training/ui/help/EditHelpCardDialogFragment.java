package com.capstone.autism_training.ui.help;

import android.content.res.Configuration;
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
import com.capstone.autism_training.databinding.DialogFragmentEditHelpCardBinding;
import com.capstone.autism_training.help.HelpCardModel;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;

public class EditHelpCardDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = EditHelpCardDialogFragment.class.getSimpleName();

    public HelpFragment helpFragment;
    private final boolean demoMode;
    private ActivityResultLauncher<String> mGetContent;
    private HelpCardModel helpCardModel;
    private byte[] image = null;
    private int adapterPosition = -1;

    private DialogFragmentEditHelpCardBinding binding;

    public EditHelpCardDialogFragment(boolean demoMode) {
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
        binding = DialogFragmentEditHelpCardBinding.inflate(inflater, container, false);

        helpFragment = (HelpFragment) getParentFragment();
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

        binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(helpCardModel.image, getResources().getDisplayMetrics().density));
        binding.nameEditText.setText(helpCardModel.name);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        if (getContext() != null && uri != null) {
                            image = ImageHelper.getBitmapAsByteArray(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri)));
                            binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image, getResources().getDisplayMetrics().density));
                        }
                    } catch (FileNotFoundException e) {
                        Snackbar.make(view, "Image not found!", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view1 -> {}).show();
                        e.printStackTrace();
                    }
                });

        binding.selectImageButton.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.editHelpCardButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;

            if (image != null && !nameEditText.getText().toString().isEmpty()) {
                long rowsAffected;
                if (demoMode) {
                    rowsAffected = 1;
                }
                else {
                    rowsAffected = helpFragment.helpCardTableManager.update(helpCardModel.id, nameEditText.getText().toString(), image);
                }
                if (rowsAffected > 0) {
                    HelpCardModel newHelpCardModel = new HelpCardModel(helpCardModel.id, nameEditText.getText().toString(), image);
                    helpFragment.mAdapter.changeItem(adapterPosition, newHelpCardModel);
                    if (getParentFragment() != null && getParentFragment().getView() != null) {
                        Snackbar.make(getParentFragment().getView(), "Successfully edited the help card", Snackbar.LENGTH_LONG)
                                .setAction("OKAY", view2 -> {}).show();
                    }
                    this.dismiss();
                }
                else {
                    Snackbar.make(view, "Error occurred while editing the help card", Snackbar.LENGTH_LONG)
                            .setAction("OKAY", view2 -> {}).show();
                }
            }
            else {
                Snackbar.make(view, "All fields are necessary", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
            }
        });
    }

    public void setHelpCardModel(HelpCardModel helpCardModel) {
        this.helpCardModel = helpCardModel;
        this.image = helpCardModel.image;
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
