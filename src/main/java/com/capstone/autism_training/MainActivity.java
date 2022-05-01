package com.capstone.autism_training;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.capstone.autism_training.databinding.ActivityMainBinding;
import com.capstone.autism_training.ui.activity.ActivityFragment;
import com.capstone.autism_training.ui.deck.CardFragment;
import com.capstone.autism_training.ui.deck.DeckFragment;
import com.capstone.autism_training.ui.help.HelpFragment;
import com.capstone.autism_training.ui.schedule.ScheduleFragment;
import com.capstone.autism_training.ui.train.TrainFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ScheduleFragment scheduleFragment;
    private DeckFragment deckFragment;
    private ActivityFragment activityFragment;
    private HelpFragment helpFragment;
    private TrainFragment trainFragment;

    private NavigationBarView navigationBarView;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
        if (scheduleFragment == null) {
            scheduleFragment = new ScheduleFragment();
        }
        deckFragment = (DeckFragment) getSupportFragmentManager().findFragmentByTag(DeckFragment.TAG);
        if (deckFragment == null) {
            deckFragment = new DeckFragment();
        }
        activityFragment = (ActivityFragment) getSupportFragmentManager().findFragmentByTag(ActivityFragment.TAG);
        if (activityFragment == null) {
            activityFragment = new ActivityFragment();
        }
        helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(HelpFragment.TAG);
        if (helpFragment == null) {
            helpFragment = new HelpFragment();
        }
        trainFragment = (TrainFragment) getSupportFragmentManager().findFragmentByTag(TrainFragment.TAG);
        if (trainFragment == null) {
            trainFragment = new TrainFragment();
        }

        if (binding.navView != null) {
            navigationBarView = binding.navView;
        }
        else if (binding.navigationRail != null) {
            navigationBarView = binding.navigationRail;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, scheduleFragment, ScheduleFragment.TAG)
                    .addToBackStack(ScheduleFragment.TAG)
                    .setReorderingAllowed(true)
                    .commit();
        }
        else if (savedInstanceState.containsKey("currentDestination")) {
            if (savedInstanceState.getInt("currentDestination") == R.id.navigation_deck) {
                navigationBarView.setSelectedItemId(R.id.navigation_deck);
            }
            else if (savedInstanceState.getInt("currentDestination") == R.id.navigation_activity) {
                navigationBarView.setSelectedItemId(R.id.navigation_activity);
            }
            else if (savedInstanceState.getInt("currentDestination") == R.id.navigation_help) {
                navigationBarView.setSelectedItemId(R.id.navigation_help);
            }
            else if (savedInstanceState.getInt("currentDestination") == R.id.navigation_train) {
                navigationBarView.setSelectedItemId(R.id.navigation_train);
            }
        }

        navigationBarView.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.navigation_schedule) {
                if (!scheduleFragment.isAdded()) {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, scheduleFragment, ScheduleFragment.TAG)
                            .addToBackStack(ScheduleFragment.TAG)
                            .setReorderingAllowed(true);
                }
                else if (!scheduleFragment.isVisible()) {
                    transaction.show(scheduleFragment).addToBackStack(ScheduleFragment.TAG).setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_deck) {
                if (!deckFragment.isAdded()) {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, deckFragment, DeckFragment.TAG)
                            .addToBackStack(DeckFragment.TAG)
                            .setReorderingAllowed(true);
                }
                else if (!deckFragment.isVisible()) {
                    transaction.show(deckFragment).addToBackStack(DeckFragment.TAG).setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_activity) {
                if (!activityFragment.isAdded()) {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, activityFragment, ActivityFragment.TAG)
                            .addToBackStack(ActivityFragment.TAG)
                            .setReorderingAllowed(true);
                }
                else if (!activityFragment.isVisible()) {
                    transaction.show(activityFragment).addToBackStack(ActivityFragment.TAG).setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_help) {
                if (!helpFragment.isAdded()) {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, helpFragment, HelpFragment.TAG)
                            .addToBackStack(HelpFragment.TAG)
                            .setReorderingAllowed(true);
                }
                else if (!helpFragment.isVisible()) {
                    transaction.show(helpFragment).addToBackStack(HelpFragment.TAG).setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_train) {
                if (!trainFragment.isAdded()) {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, trainFragment, TrainFragment.TAG)
                            .addToBackStack(TrainFragment.TAG)
                            .setReorderingAllowed(true);
                }
                else if (!trainFragment.isVisible()) {
                    transaction.show(trainFragment).addToBackStack(TrainFragment.TAG).setReorderingAllowed(true);
                }
            }

            if (!transaction.isEmpty()) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment.isVisible()) {
                        transaction.hide(fragment);
                    }
                }
                transaction.commit();
            }
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentDestination", navigationBarView.getSelectedItemId());
    }

    @Override
    public void onBackPressed() {
        if (DeckFragment.TAG.equals(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName())) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CardFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                if (fragment.getArguments() == null) {
                    fragment.setArguments(new Bundle());
                }
                fragment.getArguments().putBoolean("ON_BACK_PRESSED", true);
            }
        }

        super.onBackPressed();
        int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size == 0) {
            super.onBackPressed();
            return;
        }

        String tag = getSupportFragmentManager().getBackStackEntryAt(size - 1).getName();
        if (ScheduleFragment.TAG.equals(tag)) {
            navigationBarView.getMenu().findItem(R.id.navigation_schedule).setChecked(true);
        }
        else if (DeckFragment.TAG.equals(tag)) {
            navigationBarView.getMenu().findItem(R.id.navigation_deck).setChecked(true);
        }
        else if (ActivityFragment.TAG.equals(tag)) {
            navigationBarView.getMenu().findItem(R.id.navigation_activity).setChecked(true);
        }
        else if (HelpFragment.TAG.equals(tag)) {
            navigationBarView.getMenu().findItem(R.id.navigation_help).setChecked(true);
        }
        else if (TrainFragment.TAG.equals(tag)) {
            navigationBarView.getMenu().findItem(R.id.navigation_train).setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}