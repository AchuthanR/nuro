package com.capstone.autism_training;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.capstone.autism_training.databinding.ActivityMainBinding;
import com.capstone.autism_training.ui.activity.ActivityFragment;
import com.capstone.autism_training.ui.deck.DeckFragment;
import com.capstone.autism_training.ui.help.HelpFragment;
import com.capstone.autism_training.ui.schedule.ScheduleFragment;
import com.capstone.autism_training.ui.training.TrainingFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Fragment navHostFragment;
    private DeckFragment deckFragment;
    private ScheduleFragment scheduleFragment;
    private ActivityFragment activityFragment;
    private HelpFragment helpFragment;
    private TrainingFragment trainingFragment;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        deckFragment = new DeckFragment();
        scheduleFragment = new ScheduleFragment();
        activityFragment = new ActivityFragment();
        helpFragment = new HelpFragment();
        trainingFragment = new TrainingFragment();

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
        navGraph.setStartDestination(R.id.navigation_deck);
        navController.setGraph(navGraph);
        navHostFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, deckFragment, DeckFragment.TAG)
                .addToBackStack(DeckFragment.TAG)
                .setReorderingAllowed(true)
                .commit();

        binding.navView.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = navHostFragment.getChildFragmentManager().beginTransaction();
            for (Fragment fragment : navHostFragment.getChildFragmentManager().getFragments()) {
                if (fragment.isVisible()) {
                    transaction.hide(fragment);
                }
            }

            if (item.getItemId() == R.id.navigation_deck) {
                if (deckFragment.isAdded()) {
                    transaction.show(deckFragment).addToBackStack(DeckFragment.TAG).setReorderingAllowed(true);
                }
                else {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, deckFragment, DeckFragment.TAG)
                            .addToBackStack(DeckFragment.TAG)
                            .setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_schedule) {
                if (scheduleFragment.isAdded()) {
                    transaction.show(scheduleFragment).addToBackStack(ScheduleFragment.TAG).setReorderingAllowed(true);
                }
                else {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, scheduleFragment, ScheduleFragment.TAG)
                            .addToBackStack(ScheduleFragment.TAG)
                            .setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_activity) {
                if (activityFragment.isAdded()) {
                    transaction.show(activityFragment).addToBackStack(ActivityFragment.TAG).setReorderingAllowed(true);
                }
                else {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, activityFragment, ActivityFragment.TAG)
                            .addToBackStack(ActivityFragment.TAG)
                            .setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_help) {
                if (helpFragment.isAdded()) {
                    transaction.show(helpFragment).addToBackStack(HelpFragment.TAG).setReorderingAllowed(true);
                }
                else {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, helpFragment, HelpFragment.TAG)
                            .addToBackStack(HelpFragment.TAG)
                            .setReorderingAllowed(true);
                }
            }
            else if (item.getItemId() == R.id.navigation_training) {
                if (trainingFragment.isAdded()) {
                    transaction.show(trainingFragment).addToBackStack(TrainingFragment.TAG).setReorderingAllowed(true);
                }
                else {
                    transaction
                            .add(R.id.nav_host_fragment_activity_main, trainingFragment, TrainingFragment.TAG)
                            .addToBackStack(TrainingFragment.TAG)
                            .setReorderingAllowed(true);
                }
            }
            transaction.commit();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (navHostFragment != null) {
            navHostFragment.getChildFragmentManager().popBackStackImmediate();
            int size = navHostFragment.getChildFragmentManager().getBackStackEntryCount();
            if (size == 0) {
                super.onBackPressed();
                return;
            }

            String tag = navHostFragment.getChildFragmentManager().getBackStackEntryAt(size - 1).getName();
            if (DeckFragment.TAG.equals(tag)) {
                binding.navView.getMenu().findItem(R.id.navigation_deck).setChecked(true);
            }
            else if (ScheduleFragment.TAG.equals(tag)) {
                binding.navView.getMenu().findItem(R.id.navigation_schedule).setChecked(true);
            }
            else if (ActivityFragment.TAG.equals(tag)) {
                binding.navView.getMenu().findItem(R.id.navigation_activity).setChecked(true);
            }
            else if (HelpFragment.TAG.equals(tag)) {
                binding.navView.getMenu().findItem(R.id.navigation_help).setChecked(true);
            }
            else if (TrainingFragment.TAG.equals(tag)) {
                binding.navView.getMenu().findItem(R.id.navigation_training).setChecked(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}