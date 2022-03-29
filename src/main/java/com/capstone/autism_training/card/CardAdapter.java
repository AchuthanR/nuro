package com.capstone.autism_training.card;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<CardModel> cards;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView captionTextView;
        private final Button showAnswerButton;
        private final TextView answerTextView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            captionTextView = view.findViewById(R.id.captionTextView);
            showAnswerButton = view.findViewById(R.id.showAnswerButton);
            answerTextView = view.findViewById(R.id.answerTextView);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getCaptionTextView() {
            return captionTextView;
        }

        public Button getShowAnswerButton() {
            return showAnswerButton;
        }

        public TextView getAnswerTextView() {
            return answerTextView;
        }
    }

    public CardAdapter(ArrayList<CardModel> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(cards.get(position).image, 0, cards.get(position).image.length, options1);

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
        viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(cards.get(position).image, 0, cards.get(position).image.length, options2));
        viewHolder.getCaptionTextView().setText(cards.get(position).caption);
        viewHolder.getAnswerTextView().setText(cards.get(position).answer);

        viewHolder.getShowAnswerButton().setOnClickListener(view1 -> {
            if (viewHolder.answerTextView.getVisibility() == View.GONE) {
                viewHolder.answerTextView.setVisibility(View.VISIBLE);
                viewHolder.getShowAnswerButton().setText(R.string.hide_answer_button_text_activity_card);
            }
            else {
                viewHolder.answerTextView.setVisibility(View.GONE);
                viewHolder.getShowAnswerButton().setText(R.string.show_answer_button_text_activity_card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}