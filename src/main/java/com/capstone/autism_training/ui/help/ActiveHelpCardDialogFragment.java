package com.capstone.autism_training.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.databinding.FragmentActiveHelpCardBinding;
import com.capstone.autism_training.utilities.ImageHelper;

public class ActiveHelpCardDialogFragment extends DialogFragment {

    public static final String TAG = ActiveHelpCardDialogFragment.class.getSimpleName();

    private FragmentActiveHelpCardBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActiveHelpCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            byte[] image = getArguments().getByteArray("image");
            String name = getArguments().getString("name");
            binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));
            binding.nameTextView.setText(name);
        }

        binding.linearLayout.setOnClickListener(view1 -> ActiveHelpCardDialogFragment.this.dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
        {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
