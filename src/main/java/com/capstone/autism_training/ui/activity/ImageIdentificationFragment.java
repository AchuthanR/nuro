package com.capstone.autism_training.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.common.MyArrayAdapter;
import com.capstone.autism_training.databinding.FragmentImageIdentificationBinding;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ImageIdentificationFragment extends Fragment {

    public static final String TAG = ImageIdentificationFragment.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private DeckTableManager deckTableManager;
    private Cursor cursor;
    private ArrayList<Integer> cardPositions;
    private int currentAnswerIndex;
    private int correctOption;

    private FragmentImageIdentificationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageIdentificationBinding.inflate(inflater, container, false);
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
        binding.toolbar.getMenu().findItem(R.id.action_play_sound).setChecked(sharedPreferences.getBoolean("playSoundImageIdentification", true));
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_play_sound) {
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("playSoundImageIdentification", item.isChecked());
                editor.apply();
                return true;
            }
            return false;
        });

        DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getContext());
        deckInfoTableManager.open();
        cursor = deckInfoTableManager.fetch();
        cursor.moveToLast();
        ArrayList<String> decks = new ArrayList<>();
        while (!cursor.isBeforeFirst() || cursor.isLast()) {
            int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
            decks.add(cursor.getString(nameIndex));
            cursor.moveToPrevious();
        }
        cursor.close();

        deckTableManager = new DeckTableManager(getContext());
        cardPositions = new ArrayList<>();

        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseDeckAutoCompleteTextView.setAdapter(adapter);
        binding.chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> deckSelected(adapterView.getItemAtPosition(i).toString()));

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
                if (sharedPreferences != null && sharedPreferences.getBoolean("playSoundImageIdentification", true)) {
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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!binding.chooseDeckAutoCompleteTextView.getText().toString().isEmpty()) {
            deckSelected(binding.chooseDeckAutoCompleteTextView.getText().toString());
        }
    }

    private void deckSelected(String deck) {
        deckTableManager.open(deck);
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
            if (getView() != null) {
                Snackbar.make(getView(), "At least 4 cards are needed in a deck to perform this activity", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OKAY", view2 -> {
                        }).show();
            }
        }
    }

    private void nextQuestion() {
        binding.buttonToggleGroup.clearChecked();
        binding.nextButton.setEnabled(false);

        Random random = new Random();
        int answerPosition = cardPositions.get(currentAnswerIndex);
        int imageColumnIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int answerColumnIndex = cursor.getColumnIndex(DeckTableHelper.SHORT_ANSWER);

        ArrayList<Integer> otherPositions = new ArrayList<>(cardPositions);
        otherPositions.remove(currentAnswerIndex);

        ArrayList<byte[]> images = new ArrayList<>();
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        images.add(cursor.getBlob(imageColumnIndex));
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        images.add(cursor.getBlob(imageColumnIndex));
        cursor.moveToPosition(otherPositions.remove(random.nextInt(otherPositions.size())));
        images.add(cursor.getBlob(imageColumnIndex));
        cursor.moveToPosition(answerPosition);
        correctOption = random.nextInt(4);
        images.add(correctOption, cursor.getBlob(imageColumnIndex));

        binding.questionTextView.setText(String.format(getString(R.string.identify_question_text_view_text_fragment_image_identification), cursor.getString(answerColumnIndex)));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.imageView1.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(0)));
            binding.imageView2.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(1)));
            binding.imageView3.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(2)));
            binding.imageView4.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(3)));
        }
        else {
            binding.imageView1.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(0), 500));
            binding.imageView2.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(1), 500));
            binding.imageView3.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(2), 500));
            binding.imageView4.setImageBitmap(ImageHelper.toCompressedBitmap(images.get(3), 500));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        cursor.close();
        mediaPlayer.stop();
    }
}