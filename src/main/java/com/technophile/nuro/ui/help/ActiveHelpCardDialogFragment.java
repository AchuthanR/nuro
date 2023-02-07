package com.technophile.nuro.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.technophile.nuro.databinding.DialogFragmentActiveHelpCardBinding;
import com.technophile.nuro.utils.ImageHelper;

public class ActiveHelpCardDialogFragment extends DialogFragment {

    public static final String TAG = ActiveHelpCardDialogFragment.class.getSimpleName();

    private DialogFragmentActiveHelpCardBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogFragmentActiveHelpCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            byte[] image = getArguments().getByteArray("image");
            binding.imageView.setImageBitmap(ImageHelper.toBitmap(image));
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
