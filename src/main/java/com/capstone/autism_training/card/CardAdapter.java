package com.capstone.autism_training.card;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final ArrayList<CardModel> cards;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final TextView captionTextView;
        private final MaterialButton showAnswerButton;
        private final TextView answerTextView;
        private final CardItemDetails cardItemDetails;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            captionTextView = view.findViewById(R.id.captionTextView);
            showAnswerButton = view.findViewById(R.id.showAnswerButton);
            answerTextView = view.findViewById(R.id.answerTextView);
            cardItemDetails = new CardItemDetails();
        }

        public MaterialCardView getCardView() {
            return cardView;
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

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            cardItemDetails.setPosition(getAdapterPosition());
            cardItemDetails.setSelectionKey(cards.get(getAdapterPosition()).id);
            return cardItemDetails;
        }

        public final void bind(boolean isActive) {
            cardView.setChecked(isActive);
        }
    }

    public CardAdapter() {
        cards = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).id;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_card_item, viewGroup, false);

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

        viewHolder.bind(selectionTracker.isSelected(cards.get(position).id));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void addItem(CardModel cardModel) {
        cards.add(0, cardModel);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
    }
}