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
import com.capstone.autism_training.databinding.FragmentEditDeckBinding;
import com.capstone.autism_training.deck.DeckModel;
import com.capstone.autism_training.utilities.ImageHelper;

import java.io.FileNotFoundException;

public class EditDeckDialogFragment extends DialogFragment {

    public static final String TAG = EditDeckDialogFragment.class.getSimpleName();

    private ActivityResultLauncher<String> mGetContent;
    private DeckFragment deckFragment;
    private DeckModel deckModel;
    private byte[] image = null;
    private int adapterPosition = -1;

    private FragmentEditDeckBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_App);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditDeckBinding.inflate(inflater, container, false);

        deckFragment = (DeckFragment) getParentFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(view1 -> this.dismiss());

        binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(deckModel.image));
        binding.nameEditText.setText(deckModel.name);
        binding.descriptionEditText.setText(deckModel.description);

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

        binding.editDeckButton.setOnClickListener(view1 -> {
            EditText nameEditText = binding.nameEditText;
            EditText descriptionEditText = binding.descriptionEditText;

            if (image != null && !nameEditText.getText().toString().isEmpty() && !descriptionEditText.getText().toString().isEmpty()) {
                long rowsAffected = deckFragment.deckInfoTableManager.update(deckModel.id, deckModel.name, nameEditText.getText().toString(), image, descriptionEditText.getText().toString());
                if (rowsAffected > 0) {
                    DeckModel newDeckModel = new DeckModel(deckModel.id, image, nameEditText.getText().toString(), descriptionEditText.getText().toString());
                    deckFragment.mAdapter.changeItem(adapterPosition, newDeckModel);
                    Toast.makeText(getContext(), "Successfully edited the deck", Toast.LENGTH_LONG).show();
                    this.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Error while editing the deck", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getContext(), "All fields are necessary", Toast.LENGTH_LONG).show();
            }
        });
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