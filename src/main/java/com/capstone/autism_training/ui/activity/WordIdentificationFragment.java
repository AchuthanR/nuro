package com.capstone.autism_training.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.common.MyArrayAdapter;
import com.capstone.autism_training.databinding.FragmentWordIdentificationBinding;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WordIdentificationFragment extends Fragment {

    public static final String TAG = WordIdentificationFragment.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private DeckTableManager deckTableManager;
    private Cursor cursor;
    private ArrayList<Integer> cardPositions;
    private int currentAnswerIndex;
    private int correctOption;

    private FragmentWordIdentificationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWordIdentificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.cheer);
        if (getContext() != null) {
            sharedPreferences = getContext().getSharedPreferences("activities", Context.MODE_PRIVATE);
        }
        binding.toolbar.getMenu().findItem(R.id.action_play_sound).setChecked(sharedPreferences.getBoolean("playSoundWordIdentification", true));
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_play_sound) {
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("playSoundWordIdentification", item.isChecked());
                editor.apply();
                return true;
            }
            return false;
        });

        DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getContext());
        deckInfoTableManager.open();
        cursor = deckInfoTableManager.fetch();
        ArrayList<String> decks = new ArrayList<>();
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
            decks.add(cursor.getString(nameIndex));
            cursor.moveToNext();
        }
        cursor.close();

        deckTableManager = new DeckTableManager(getContext());
        cardPositions = new ArrayList<>();

        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseDeckAutoCompleteTextView.setAdapter(adapter);
        binding.chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            deckTableManager.open(adapterView.getItemAtPosition(i).toString());
            if (!cursor.isClosed()) {
                cursor.close();
            }
            cursor = deckTableManager.fetch();
            deckTableManager.close();

            if (cursor.getCount() >= 4) {
                cardPositions.clear();
                for (int j=0; j<cursor.getCount(); j++) {
                    cardPositions.add(j);
                }
                Collections.shuffle(cardPositions);
                currentAnswerIndex = 0;

                nextQuestion();

                if (binding.activityLinearLayout.getVisibility() == View.GONE) {
                    binding.activityLinearLayout.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (binding.activityLinearLayout.getVisibility() != View.GONE) {
                    binding.activityLinearLayout.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "At least 4 cards are needed in a deck to perform this activity", Toast.LENGTH_LONG).show();
            }
        });

        binding.submitButton.setOnClickListener(view1 -> {
            int id = binding.buttonToggleGroup.getCheckedButtonId();
            if (id == View.NO_ID) {
                Snackbar.make(view1, "Please select an answer", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
                return;
            }

            binding.nextButton.setEnabled(true);

            boolean correctAnswer = false;
            switch (correctOption) {
                case 0:
                    if (id == R.id.option1) {
                        correctAnswer = true;
                    }
                    break;
                case 1:
                    if (id == R.id.option2) {
                        correctAnswer = true;
                    }
                    break;
                case 2:
                    if (id == R.id.option3) {
                        correctAnswer = true;
                    }
                    break;
                case 3:
                    if (id == R.id.option4) {
                        correctAnswer = true;
                    }
                    break;
            }

            if (correctAnswer) {
                Snackbar.make(view1, "Correct answer", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
                if (sharedPreferences != null && sharedPreferences.getBoolean("playSoundWordIdentification", true)) {
                    mediaPlayer.start();
                }
            }
            else {
                Snackbar.make(view1, "Wrong answer", Snackbar.LENGTH_LONG)
                        .setAction("OKAY", view2 -> {}).show();
            }
        });

        binding.nextButton.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            currentAnswerIndex++;
            if (currentAnswerIndex == cardPositions.size()) {
                Snackbar.make(view1, "You have come to the end of the deck", Snackbar.LENGTH_LONG).show();
                binding.activityLinearLayout.setVisibility(View.GONE);
            }
            else {
                nextQuestion();
            }
            binding.nestedScrollView.fullScroll(View.FOCUS_UP);
        });
    }

    private void nextQuestion() {
        binding.buttonToggleGroup.clearChecked();
        binding.nextButton.setEnabled(false);

        Random random = new Random();
        int answerPosition = cardPositions.get(currentAnswerIndex);
        int imageColumnIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int answerColumnIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);

        ArrayList<Integer> otherPositions = new ArrayList<>(cardPositions);
        otherPositions.remove(currentAnswerIndex);

        ArrayList<String> answers = new ArrayList<>();
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        answers.add(cursor.getString(answerColumnIndex));
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        answers.add(cursor.getString(answerColumnIndex));
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        answers.add(cursor.getString(answerColumnIndex));
        cursor.moveToPosition(answerPosition);
        correctOption = random.nextInt(4);
        answers.add(correctOption, cursor.getString(answerColumnIndex));
        byte[] image = cursor.getBlob(imageColumnIndex);
        binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(image));

        binding.option1.setText(answers.get(0));

        binding.option2.setText(answers.get(1));

        binding.option3.setText(answers.get(2));

        binding.option4.setText(answers.get(3));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        cursor.close();
        mediaPlayer.stop();
    }
}