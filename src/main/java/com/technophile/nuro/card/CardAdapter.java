package com.technophile.nuro.card;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.technophile.nuro.R;
import com.technophile.nuro.utils.ImageHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final ArrayList<CardModel> cards;
    private SelectionTracker<Long> selectionTracker = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ShapeableImageView imageView;
        private final MaterialTextView captionTextView;
        private final MaterialButton showAnswerButton;
        private final MaterialTextView shortAnswerTextView;
        private final CardItemDetails cardItemDetails;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            captionTextView = view.findViewById(R.id.captionTextView);
            showAnswerButton = view.findViewById(R.id.showAnswerButton);
            shortAnswerTextView = view.findViewById(R.id.shortAnswerTextView);
            cardItemDetails = new CardItemDetails();
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ShapeableImageView getImageView() {
            return imageView;
        }

        public MaterialTextView getCaptionTextView() {
            return captionTextView;
        }

        public MaterialButton getShowAnswerButton() {
            return showAnswerButton;
        }

        public MaterialTextView getShortAnswerTextView() {
            return shortAnswerTextView;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            cardItemDetails.setPosition(getAdapterPosition());
            cardItemDetails.setSelectionKey(cards.get(getAdapterPosition()).id);
            return cardItemDetails;
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

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.getShowAnswerButton().setOnClickListener(view1 -> {
            if (viewHolder.shortAnswerTextView.getVisibility() == View.GONE) {
                viewHolder.shortAnswerTextView.setVisibility(View.VISIBLE);
                viewHolder.getShowAnswerButton().setText(R.string.hide_answer_button_text_fragment_card);
                viewHolder.getShowAnswerButton().setIconResource(R.drawable.round_visibility_off_24);
            }
            else {
                viewHolder.shortAnswerTextView.setVisibility(View.GONE);
                viewHolder.getShowAnswerButton().setText(R.string.show_answer_button_text_fragment_card);
                viewHolder.getShowAnswerButton().setIconResource(R.drawable.round_visibility_24);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageBitmap(ImageHelper.toBitmap(cards.get(position).image));
        viewHolder.getCaptionTextView().setText(cards.get(position).caption);
        viewHolder.getShortAnswerTextView().setText(cards.get(position).short_answer);

        if (selectionTracker != null) {
            viewHolder.getCardView().setChecked(selectionTracker.isSelected(cards.get(position).id));
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public long getMaxId() {
        Optional<CardModel> result = cards.stream().max(Comparator.comparingLong(cardModel -> cardModel.id));
        if (result.isPresent()) {
            return result.get().id;
        }
        else {
            return getItemCount();
        }
    }

    public CardModel getItem(int position) {
        return cards.get(position);
    }

    public void addItem(CardModel cardModel) {
        cards.add(0, cardModel);
        notifyItemInserted(0);
    }

    public void changeItem(int position, CardModel newCardModel) {
        cards.set(position, newCardModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAll() {
        int size = cards.size();
        cards.clear();
        notifyItemRangeRemoved(0, size);
    }
}