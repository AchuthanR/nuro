package com.capstone.autism_training.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.capstone.autism_training.R;
import com.capstone.autism_training.databinding.FragmentActivityBinding;
import com.capstone.autism_training.ui.deck.DeckFragment;

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
            ImageIdentificationFragment imageIdentificationFragment = null;

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            for (Fragment fragment : getParentFragmentManager().getFragments()) {
                if (fragment.isVisible()) {
                    transaction.hide(fragment);
                }
                if (ImageIdentificationFragment.TAG.equals(fragment.getTag()) && fragment.isAdded()) {
                    imageIdentificationFragment = (ImageIdentificationFragment) fragment;
                }
            }

            if (imageIdentificationFragment != null) {
                transaction.show(imageIdentificationFragment).addToBackStack(ActivityFragment.TAG).setReorderingAllowed(true);
            }
            else {
                imageIdentificationFragment = new ImageIdentificationFragment();
                transaction
                        .add(R.id.nav_host_fragment_activity_main, imageIdentificationFragment, ImageIdentificationFragment.TAG)
                        .addToBackStack(ActivityFragment.TAG)
                        .setReorderingAllowed(true);
            }
            transaction.commit();
        });

        binding.wordIdentificationCardView.setOnClickListener(view1 -> {
            WordIdentificationFragment wordIdentificationFragment = null;

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            for (Fragment fragment : getParentFragmentManager().getFragments()) {
                if (fragment.isVisible()) {
                    transaction.hide(fragment);
                }
                if (WordIdentificationFragment.TAG.equals(fragment.getTag()) && fragment.isAdded()) {
                    wordIdentificationFragment = (WordIdentificationFragment) fragment;
                }
            }

            if (wordIdentificationFragment != null) {
                transaction.show(wordIdentificationFragment).addToBackStack(ActivityFragment.TAG).setReorderingAllowed(true);
            }
            else {
                wordIdentificationFragment = new WordIdentificationFragment();
                transaction
                        .add(R.id.nav_host_fragment_activity_main, wordIdentificationFragment, WordIdentificationFragment.TAG)
                        .addToBackStack(ActivityFragment.TAG)
                        .setReorderingAllowed(true);
            }
            transaction.commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}