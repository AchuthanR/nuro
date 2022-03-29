package com.capstone.autism_training.deck;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.CardActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private ArrayList<DeckInfo> decks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            titleTextView = view.findViewById(R.id.titleTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
        }

        public Context getContext() {
            return context;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }
    }

    public DeckAdapter(ArrayList<DeckInfo> decks) {
        this.decks = decks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.deck_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decks.get(position).image, 0, decks.get(position).image.length, options1);

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
        viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(decks.get(position).image, 0, decks.get(position).image.length, options2));
        viewHolder.getTitleTextView().setText(decks.get(position).name);
        viewHolder.getDescriptionTextView().setText(decks.get(position).description);

        viewHolder.getCardView().setOnClickListener(view1 -> {
            Intent intent = new Intent(viewHolder.getContext(), CardActivity.class);
            intent.putExtra("TABLE_NAME", decks.get(position).name);
            viewHolder.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }
}