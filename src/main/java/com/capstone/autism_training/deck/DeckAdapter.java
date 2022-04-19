package com.capstone.autism_training.deck;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.ui.deck.CardFragment;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private final ArrayList<DeckModel> decks;
    private final FragmentManager fragmentManager;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final MaterialTextView titleTextView;
        private final MaterialTextView descriptionTextView;
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

        public MaterialTextView getTitleTextView() {
            return titleTextView;
        }

        public MaterialTextView getDescriptionTextView() {
            return descriptionTextView;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            deckItemDetails.setPosition(getAdapterPosition());
            deckItemDetails.setSelectionKey(decks.get(getAdapterPosition()).id);
            return deckItemDetails;
        }
    }

    public DeckAdapter(FragmentManager fragmentManager) {
        decks = new ArrayList<>();
        this.fragmentManager = fragmentManager;
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

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.getCardView().setOnClickListener(view1 -> {
            CardFragment cardFragment = new CardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TABLE_NAME", decks.get(viewHolder.getAdapterPosition()).name);
            cardFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, cardFragment, CardFragment.TAG)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageBitmap(ImageHelper.toCompressedBitmap(decks.get(position).image));
        viewHolder.getTitleTextView().setText(decks.get(position).name);
        viewHolder.getDescriptionTextView().setText(decks.get(position).description);

        viewHolder.getCardView().setChecked(selectionTracker.isSelected(decks.get(position).id));
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