package com.capstone.autism_training.deck;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.CardActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private final ArrayList<DeckModel> decks;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final DeckItemDetails deckItemDetails;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            titleTextView = view.findViewById(R.id.titleTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            deckItemDetails = new DeckItemDetails();
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

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            deckItemDetails.setPosition(getAdapterPosition());
            deckItemDetails.setSelectionKey(decks.get(getAdapterPosition()).id);
            return deckItemDetails;
        }

        public final void bind(boolean isActive) {
            cardView.setChecked(isActive);
        }
    }

    public DeckAdapter() {
        decks = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return decks.get(position).id;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_deck_item, viewGroup, false);

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
            intent.putExtra("TABLE_NAME", decks.get(viewHolder.getAdapterPosition()).name);
            viewHolder.getContext().startActivity(intent);
        });

        viewHolder.bind(selectionTracker.isSelected(decks.get(position).id));
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    public void addItem(DeckModel deckModel) {
        decks.add(0, deckModel);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        decks.remove(position);
        notifyItemRemoved(position);
    }
}