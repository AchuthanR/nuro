package com.capstone.autism_training.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;

public class IdentificationActivity extends AppCompatActivity {

    private DeckTableManager deckTableManager;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, decks);
        MaterialAutoCompleteTextView materialAutoCompleteTextView = findViewById(R.id.chooseDeckEditText);
        materialAutoCompleteTextView.setAdapter(adapter);
        materialAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            deckTableManager.open(adapterView.getItemAtPosition(i).toString().replace(" ", "_"));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            cursor = deckTableManager.fetch();

            if (cursor.getCount() >= 4) {
                int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
                byte[] image = cursor.getBlob(imageIndex);
                ImageView imageView1 = findViewById(R.id.imageView1);
                imageView1.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

                cursor.moveToNext();
                image = cursor.getBlob(imageIndex);
                ImageView imageView2 = findViewById(R.id.imageView2);
                imageView2.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

                cursor.moveToNext();
                image = cursor.getBlob(imageIndex);
                ImageView imageView3 = findViewById(R.id.imageView3);
                imageView3.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

                cursor.moveToNext();
                int answerIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);
                String answer = cursor.getString(answerIndex);
                TextView textView = findViewById(R.id.questionTextView);
                textView.setText("Find " + answer);

                image = cursor.getBlob(imageIndex);
                ImageView imageView4 = findViewById(R.id.imageView4);
                imageView4.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
                cursor.moveToFirst();
            }
            else {

                Toast.makeText(getApplicationContext(), "At least 4 cards are needed in a deck to perform this activity", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), adapterView.getItemAtPosition(i).toString() + " - " + cursor.getCount() + " cards", Toast.LENGTH_LONG).show();
        });
    }
}