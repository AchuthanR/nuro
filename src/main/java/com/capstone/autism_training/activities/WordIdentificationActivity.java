package com.capstone.autism_training.activities;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WordIdentificationActivity extends AppCompatActivity {

    private DeckTableManager deckTableManager;
    private Cursor cursor;
    private ArrayList<Integer> cardPositions;
    private int currentAnswerIndex;
    private int correctOption;

    private MaterialButtonToggleGroup buttonToggleGroup;
    private MaterialButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_identification);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        DeckInfoTableManager deckInfoTableManager = new DeckInfoTableManager(getApplicationContext());
        deckInfoTableManager.open();
        cursor = deckInfoTableManager.fetch();
        ArrayList<String> decks = new ArrayList<>();
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
            decks.add(cursor.getString(nameIndex));
            cursor.moveToNext();
        }
        cursor.close();

        deckTableManager = new DeckTableManager(this);
        cardPositions = new ArrayList<>();

        MaterialAutoCompleteTextView chooseDeckAutoCompleteTextView = findViewById(R.id.chooseDeckAutoCompleteTextView);
        buttonToggleGroup = findViewById(R.id.buttonToggleGroup);
        MaterialButton submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, decks);
        chooseDeckAutoCompleteTextView.setAdapter(adapter);
        chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            deckTableManager.open(adapterView.getItemAtPosition(i).toString().replace(" ", "_"));
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

                LinearLayout activityLinearLayout = findViewById(R.id.activityLinearLayout);
                if (activityLinearLayout.getVisibility() == View.GONE) {
                    activityLinearLayout.setVisibility(View.VISIBLE);
                }
            }
            else {
                LinearLayout activityLinearLayout = findViewById(R.id.activityLinearLayout);
                if (activityLinearLayout.getVisibility() != View.GONE) {
                    activityLinearLayout.setVisibility(View.GONE);
                }
                Toast.makeText(getApplicationContext(), "At least 4 cards are needed in a deck to perform this activity", Toast.LENGTH_LONG).show();
            }
        });

        submitButton.setOnClickListener(view -> {
            int id = buttonToggleGroup.getCheckedButtonId();
            if (id == View.NO_ID) {
                Toast.makeText(getApplicationContext(), "Please select an answer!", Toast.LENGTH_LONG).show();
                return;
            }

            nextButton.setEnabled(true);

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
                Toast.makeText(getApplicationContext(), "Correct answer", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Wrong answer", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(view -> {
            currentAnswerIndex++;
            if (currentAnswerIndex == cardPositions.size()) {
                Toast.makeText(getApplicationContext(), "You have come to the end of the deck", Toast.LENGTH_LONG).show();
                LinearLayout activityLinearLayout = findViewById(R.id.activityLinearLayout);
                activityLinearLayout.setVisibility(View.GONE);
            }
            else {
                nextQuestion();
            }
            NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
            nestedScrollView.fullScroll(View.FOCUS_UP);
        });
    }

    private void nextQuestion() {
        buttonToggleGroup.clearChecked();
        nextButton.setEnabled(false);

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
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

        MaterialButton option1 = findViewById(R.id.option1);
        option1.setText(answers.get(0));

        MaterialButton option2 = findViewById(R.id.option2);
        option2.setText(answers.get(1));

        MaterialButton option3 = findViewById(R.id.option3);
        option3.setText(answers.get(2));

        MaterialButton option4 = findViewById(R.id.option4);
        option4.setText(answers.get(3));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
    }
}