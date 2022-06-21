package com.technophile.nuro.ui.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.technophile.nuro.R;
import com.technophile.nuro.databinding.FragmentActivityBinding;

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

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (binding.gridLayout != null && dpWidth > 600) {
            binding.gridLayout.setColumnCount((int) dpWidth / 400);
            ViewGroup.LayoutParams layoutParams = binding.gridLayout.getLayoutParams();
            int cardViewsInARow = Math.min(binding.gridLayout.getChildCount(), (int) dpWidth / 400);
            // Subtracting 112dp to account for the navigation rail and activity_horizontal_margin
            layoutParams.width = (int) ((dpWidth - 112) / ((int) dpWidth / 400) * cardViewsInARow * displayMetrics.density);
            binding.gridLayout.setLayoutParams(layoutParams);
        }

        binding.imageIdentificationCardView.setOnClickListener(view1 -> {
            ImageIdentificationFragment imageIdentificationFragment = null;

            getParentFragmentManager().executePendingTransactions();
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
                        .add(R.id.navHostFragmentActivityMain, imageIdentificationFragment, ImageIdentificationFragment.TAG)
                        .addToBackStack(ActivityFragment.TAG)
                        .setReorderingAllowed(true);
            }
            transaction.commit();
        });

        binding.wordIdentificationCardView.setOnClickListener(view1 -> {
            WordIdentificationFragment wordIdentificationFragment = null;

            getParentFragmentManager().executePendingTransactions();
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
                        .add(R.id.navHostFragmentActivityMain, wordIdentificationFragment, WordIdentificationFragment.TAG)
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