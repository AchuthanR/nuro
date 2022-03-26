package com.capstone.autism_training.deck;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.capstone.autism_training.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AddDeckDialogFragment extends DialogFragment {

    public static final String TAG = "AddDeckDialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_LanguageTherapyAssistanceForAutisticChildren);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_deck, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view2 -> {
            this.dismiss();
        });

        return view;
    }
}
