package com.capstone.autism_training.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class ActiveHelpCardDialogFragment extends DialogFragment {

    public static final String TAG = "ActiveHelpCardDialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_help_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            byte[] image = getArguments().getByteArray("image");
            String name = getArguments().getString("name");
            ImageView imageView = view.findViewById(R.id.imageView);
            MaterialTextView textView = view.findViewById(R.id.nameTextView);
            imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));
            textView.setText(name);
        }

        MaterialButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(view1 -> ActiveHelpCardDialogFragment.this.dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
        {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
