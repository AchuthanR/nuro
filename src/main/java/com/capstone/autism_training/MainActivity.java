package com.capstone.autism_training;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.autism_training.deck.DeckTableManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DeckTableManager deckTableManager;
    private Cursor cursor;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deckTableManager = new DeckTableManager(getApplicationContext());
        deckTableManager.open("IMAGE_DECK");

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        byte[] image = getBitmapAsByteArray(BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri)));
                        long rowNumber = deckTableManager.insert("caption", image);
                        if (rowNumber != -1) {
                            Toast.makeText(getApplicationContext(), "Successfully inserted image at row " + rowNumber + ". Fetch rows again to change image.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error while inserting image in the table", Toast.LENGTH_LONG).show();
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "File not found!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }

    public void fetchRows_onClick(View view) {
        cursor = deckTableManager.fetch();
        TextView textView = findViewById(R.id.databaseInfo);
        textView.setText(String.format(Locale.getDefault(), "%d rows fetched from the table", cursor.getCount()));

        if (cursor.getCount() == 0) {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(null);
        }
    }

    public void addImage_onClick(View view) {
        mGetContent.launch("image/*");
    }

    public void changeImage_onClick(View view) {
        if (cursor == null) {
            Toast.makeText(getApplicationContext(), "First fetch rows from the table", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView imageView = findViewById(R.id.imageView);
        if (!cursor.isAfterLast() || cursor.isFirst()) {
            byte[] image = cursor.getBlob(1);
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(image, 0, image.length, options1);

            final int REQUIRED_SIZE = 300;

            int width_tmp = options1.outWidth, height_tmp = options1.outHeight;
            int scale = 1;
            while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            options2.inJustDecodeBounds = false;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length, options2));

            cursor.moveToNext();
        }
        else {
            Toast.makeText(getApplicationContext(), "End of the table. Moving to first row", Toast.LENGTH_LONG).show();
            cursor.moveToFirst();
        }
    }

    public void deleteTable_onClick(View view) {
        deckTableManager.deleteTable();
        Toast.makeText(getApplicationContext(), "Successfully deleted the table. Fetch rows again to see the change.", Toast.LENGTH_LONG).show();
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        deckTableManager.close();
        cursor.close();
        super.onDestroy();
    }

    public void modules_onClick(View view) {
        startActivity(new Intent(this, ModuleActivity.class));
    }
}