package com.capstone.autism_training.ui.training;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.CardModel;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.common.MyArrayAdapter;
import com.capstone.autism_training.databinding.FragmentTrainingBinding;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.capstone.autism_training.training.SuperMemoTableHelper;
import com.capstone.autism_training.training.SuperMemoTableManager;
import com.capstone.autism_training.training.TrainingDeck;
import com.capstone.autism_training.utilities.ImageHelper;

import java.util.ArrayList;

public class TrainingFragment extends Fragment {

    public static final String TAG = TrainingFragment.class.getSimpleName();

    private SuperMemoTableManager superMemoTableManager;
    private TrainingDeck trainingDeck;
    private CardModel currentCard;

    private FragmentTrainingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrainingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        superMemoTableManager = new SuperMemoTableManager(getContext());
        trainingDeck = new TrainingDeck();

        DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getContext());
        deckInfoTableManager.open();
        Cursor cursor = deckInfoTableManager.fetch();
        ArrayList<String> decks = new ArrayList<>();
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
            decks.add(cursor.getString(nameIndex));
            cursor.moveToNext();
        }
        cursor.close();

        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseDeckAutoCompleteTextView.setAdapter(adapter);
        binding.chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            deckSelected(adapterView.getItemAtPosition(i).toString().replace(" ", "_"));

            if (trainingDeck.getSize() == 0) {
                if (binding.activityLinearLayout.getVisibility() != View.GONE) {
                    binding.activityLinearLayout.setVisibility(View.GONE);
                }

                binding.reviewInfoTextView.setText(R.string.empty_deck_text_view_text_fragment_training);
                if (binding.reviewInfoTextView.getVisibility() != View.VISIBLE) {
                    binding.reviewInfoTextView.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (binding.activityLinearLayout.getVisibility() != View.VISIBLE) {
                    binding.activityLinearLayout.setVisibility(View.VISIBLE);
                }

                if (binding.reviewInfoTextView.getVisibility() != View.GONE) {
                    binding.reviewInfoTextView.setVisibility(View.GONE);
                }

                nextCard();
            }
        });

        binding.showAnswerButton.setOnClickListener(view1 -> {
            view1.setVisibility(View.GONE);
            binding.answerTextView.setVisibility(View.VISIBLE);
            binding.reviewQuestionTextView.setVisibility(View.VISIBLE);
            binding.buttonToggleGroup.setVisibility(View.VISIBLE);
        });

        binding.buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> binding.nextButton.setEnabled(true));

        binding.nextButton.setOnClickListener(view1 -> nextCard());
    }

    private void deckSelected(String table_name) {
        trainingDeck.clearAllCards();
        currentCard = null;

        superMemoTableManager.close();
        superMemoTableManager.open(table_name);
        Cursor cursor = superMemoTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int answerIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);
        int repetitionsIndex = cursor.getColumnIndex(SuperMemoTableHelper.REPETITIONS);
        int intervalIndex = cursor.getColumnIndex(SuperMemoTableHelper.INTERVAL);
        int easinessIndex = cursor.getColumnIndex(SuperMemoTableHelper.EASINESS);
        int nextPracticeTimeIndex = cursor.getColumnIndex(SuperMemoTableHelper.NEXT_PRACTICE_TIME);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(answerIndex), cursor.getInt(repetitionsIndex), cursor.getInt(intervalIndex), cursor.getDouble(easinessIndex), cursor.getLong(nextPracticeTimeIndex));
            trainingDeck.addCard(cardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void nextCard() {
        if (currentCard != null) {
            trainingDeck.removeCard(currentCard);
            int quality = -1;
            if (binding.buttonToggleGroup.getCheckedButtonId() == R.id.option2) {
                quality = 0;
            }
            else if (binding.buttonToggleGroup.getCheckedButtonId() == R.id.option3) {
                quality = 1;
            }
            else if (binding.buttonToggleGroup.getCheckedButtonId() == R.id.option4) {
                quality = 3;
            }
            else if (binding.buttonToggleGroup.getCheckedButtonId() == R.id.option5) {
                quality = 4;
            }

            if (quality != -1) {
                currentCard.update(quality);
                superMemoTableManager.update(currentCard.id, currentCard.repetitions, currentCard.interval, currentCard.easiness, currentCard.nextPracticeTime);
            }
        }

        currentCard = trainingDeck.getNextCard();

        if (currentCard == null) {
            binding.activityLinearLayout.setVisibility(View.GONE);

            binding.reviewInfoTextView.setText(R.string.review_done_text_view_text_fragment_training);
            binding.reviewInfoTextView.setVisibility(View.VISIBLE);
        }
        else {
            binding.showAnswerButton.setVisibility(View.VISIBLE);
            binding.answerTextView.setVisibility(View.GONE);
            binding.reviewQuestionTextView.setVisibility(View.GONE);
            binding.buttonToggleGroup.setVisibility(View.GONE);
            binding.buttonToggleGroup.clearChecked();
            binding.nextButton.setEnabled(false);

            binding.questionTextView.setText(currentCard.caption);
            binding.imageView.setImageBitmap(ImageHelper.toCompressedBitmap(currentCard.image));
            binding.answerTextView.setText(currentCard.answer);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}