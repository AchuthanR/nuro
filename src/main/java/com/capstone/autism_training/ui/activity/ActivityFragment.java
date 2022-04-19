package com.capstone.autism_training.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.databinding.FragmentActivityBinding;

public class ActivityFragment extends Fragment {

    public static final String TAG = ActivityFragment.class.getSimpleName();

    private FragmentActivityBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageIdentificationCardView.setOnClickListener(view1 -> {
            ImageIdentificationFragment imageIdentificationFragment = new ImageIdentificationFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, imageIdentificationFragment, ImageIdentificationFragment.TAG)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        binding.wordIdentificationCardView.setOnClickListener(view1 -> {
            WordIdentificationFragment wordIdentificationFragment = new WordIdentificationFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, wordIdentificationFragment, WordIdentificationFragment.TAG)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}