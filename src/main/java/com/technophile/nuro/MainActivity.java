package com.technophile.nuro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.technophile.nuro.databinding.ActivityMainBinding;
import com.technophile.nuro.ui.activity.ActivityFragment;
import com.technophile.nuro.ui.deck.DeckFragment;
import com.technophile.nuro.ui.help.HelpFragment;
import com.technophile.nuro.ui.schedule.ScheduleFragment;
import com.technophile.nuro.ui.train.TrainFragment;

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

        SplashScreen.installSplashScreen(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        File databaseFile = new File(getApplicationContext().getDatabasePath("database").getPath());
        if (!databaseFile.exists()) {
            try {
                InputStream myInput = getApplicationContext().getAssets().open("databases/database.db");
                String fileName = getApplicationContext().getDatabasePath("database").getPath();
                OutputStream myOutput = new FileOutputStream(fileName);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

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
                    .replace(R.id.navHostFragmentActivityMain, scheduleFragment, ScheduleFragment.TAG)
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
            getSupportFragmentManager().executePendingTransactions();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.navigation_schedule) {
                if (!scheduleFragment.isAdded()) {
                    transaction
                            .add(R.id.navHostFragmentActivityMain, scheduleFragment, ScheduleFragment.TAG)
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
                            .add(R.id.navHostFragmentActivityMain, deckFragment, DeckFragment.TAG)
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
                            .add(R.id.navHostFragmentActivityMain, activityFragment, ActivityFragment.TAG)
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
                            .add(R.id.navHostFragmentActivityMain, helpFragment, HelpFragment.TAG)
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
                            .add(R.id.navHostFragmentActivityMain, trainFragment, TrainFragment.TAG)
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
        super.onBackPressed();
        int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size == 0) {
            super.onBackPressed();
            this.finish();
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