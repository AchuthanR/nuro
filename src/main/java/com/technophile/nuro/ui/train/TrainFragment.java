package com.technophile.nuro.ui.train;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.technophile.nuro.R;
import com.technophile.nuro.card.CardModel;
import com.technophile.nuro.card.DeckTableHelper;
import com.technophile.nuro.common.MyArrayAdapter;
import com.technophile.nuro.databinding.FragmentTrainBinding;
import com.technophile.nuro.deck.CollectionTableHelper;
import com.technophile.nuro.deck.CollectionTableManager;
import com.technophile.nuro.train.SuperMemoTableHelper;
import com.technophile.nuro.train.SuperMemoTableManager;
import com.technophile.nuro.train.TrainDeck;
import com.technophile.nuro.ui.deck.CardFragment;
import com.technophile.nuro.utils.ImageHelper;

import java.util.ArrayList;

public class TrainFragment extends Fragment {

    public static final String TAG = TrainFragment.class.getSimpleName();

    private SuperMemoTableManager superMemoTableManager;
    private TrainDeck trainDeck;
    private CardModel currentCard;

    private FragmentTrainBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_help) {
                CardFragment cardFragment = new CardFragment("Introduction to Train");
                Bundle bundle = new Bundle();
                bundle.putBoolean("readOnlyMode", true);
                cardFragment.setArguments(bundle);

                getParentFragmentManager().executePendingTransactions();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                for (Fragment fragment : getParentFragmentManager().getFragments()) {
                    if (fragment.isVisible()) {
                        transaction.hide(fragment);
                    }
                }
                transaction
                        .add(R.id.navHostFragmentActivityMain, cardFragment, null)
                        .addToBackStack(TrainFragment.TAG)
                        .setReorderingAllowed(true)
                        .commit();
                return true;
            }
            return false;
        });

        superMemoTableManager = new SuperMemoTableManager(getContext());
        trainDeck = new TrainDeck();

        loadDecksFromDatabase();
        binding.chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> deckSelected(adapterView.getItemAtPosition(i).toString()));

        binding.showAnswerButton.setOnClickListener(view1 -> {
            view1.setVisibility(View.GONE);
            binding.shortAnswerTextView.setVisibility(View.VISIBLE);
            binding.reviewQuestionTextView.setVisibility(View.VISIBLE);
            binding.buttonToggleGroup.setVisibility(View.VISIBLE);
        });

        binding.buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> binding.nextButton.setEnabled(true));

        binding.nextButton.setOnClickListener(view1 -> {
            nextCard();
            binding.nestedScrollView.fullScroll(View.FOCUS_UP);
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!binding.chooseDeckAutoCompleteTextView.getText().toString().isEmpty()) {
            try {
                deckSelected(binding.chooseDeckAutoCompleteTextView.getText().toString());
            }
            catch (SQLiteException ignored) {
                binding.chooseDeckAutoCompleteTextView.setText("", false);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadDecksFromDatabase();
        }
    }

    private void loadDecksFromDatabase() {
        CollectionTableManager collectionTableManager = new CollectionTableManager(getContext());
        collectionTableManager.open();
        Cursor cursor = collectionTableManager.fetch();
        cursor.moveToLast();
        ArrayList<String> decks = new ArrayList<>();
        while (!cursor.isBeforeFirst() || cursor.isLast()) {
            int nameIndex = cursor.getColumnIndex(CollectionTableHelper.NAME);
            decks.add(cursor.getString(nameIndex));
            cursor.moveToPrevious();
        }
        cursor.close();
        collectionTableManager.close();

        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseDeckAutoCompleteTextView.setAdapter(adapter);

        if (!decks.contains(binding.chooseDeckAutoCompleteTextView.getText().toString())) {
            binding.chooseDeckAutoCompleteTextView.setText("", false);
            binding.activityLinearLayout.setVisibility(View.GONE);
            binding.reviewInfoTextView.setVisibility(View.GONE);
        }
    }

    private void deckSelected(String table_name) {
        trainDeck.clearAllCards();
        currentCard = null;

        superMemoTableManager.close();
        superMemoTableManager.open(table_name);
        Cursor cursor = superMemoTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int answerIndex = cursor.getColumnIndex(DeckTableHelper.SHORT_ANSWER);
        int repetitionsIndex = cursor.getColumnIndex(SuperMemoTableHelper.REPETITIONS);
        int intervalIndex = cursor.getColumnIndex(SuperMemoTableHelper.INTERVAL);
        int easinessIndex = cursor.getColumnIndex(SuperMemoTableHelper.EASINESS);
        int nextPracticeTimeIndex = cursor.getColumnIndex(SuperMemoTableHelper.NEXT_PRACTICE_TIME);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getLong(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(answerIndex), cursor.getInt(repetitionsIndex), cursor.getInt(intervalIndex), cursor.getDouble(easinessIndex), cursor.getLong(nextPracticeTimeIndex));
            trainDeck.addCard(cardModel);
            cursor.moveToNext();
        }
        cursor.close();

        if (trainDeck.getSize() == 0) {
            if (binding.activityLinearLayout.getVisibility() != View.GONE) {
                binding.activityLinearLayout.setVisibility(View.GONE);
            }

            binding.reviewInfoTextView.setText(R.string.empty_deck_text_view_text_fragment_train);
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
    }

    private void nextCard() {
        if (currentCard != null) {
            if (binding.buttonToggleGroup.getCheckedButtonId() != R.id.option2) {
                trainDeck.removeCard(currentCard);
            }
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

        currentCard = trainDeck.getNextCard();

        if (currentCard == null) {
            binding.activityLinearLayout.setVisibility(View.GONE);

            binding.reviewInfoTextView.setText(R.string.review_done_text_view_text_fragment_train);
            binding.reviewInfoTextView.setVisibility(View.VISIBLE);
        }
        else {
            binding.showAnswerButton.setVisibility(View.VISIBLE);
            binding.shortAnswerTextView.setVisibility(View.GONE);
            binding.reviewQuestionTextView.setVisibility(View.GONE);
            binding.buttonToggleGroup.setVisibility(View.GONE);
            binding.buttonToggleGroup.clearChecked();
            binding.nextButton.setEnabled(false);

            binding.questionTextView.setText(currentCard.caption);
            binding.imageView.setImageBitmap(ImageHelper.toBitmap(currentCard.image));
            binding.shortAnswerTextView.setText(currentCard.short_answer);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        superMemoTableManager.close();
    }
}