package com.technophile.nuro.deck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.technophile.nuro.R;
import com.technophile.nuro.ui.deck.CardFragment;
import com.technophile.nuro.ui.deck.DeckFragment;
import com.technophile.nuro.utils.ImageHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private final ArrayList<DeckModel> decks;
    private final boolean demoMode;
    private final FragmentManager fragmentManager;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ShapeableImageView imageView;
        private final MaterialTextView titleTextView;
        private final MaterialTextView descriptionTextView;
        private final DeckItemDetails deckItemDetails;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            titleTextView = view.findViewById(R.id.titleTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            deckItemDetails = new DeckItemDetails();
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ShapeableImageView getImageView() {
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

    public DeckAdapter(FragmentManager fragmentManager, boolean demoMode) {
        decks = new ArrayList<>();
        this.demoMode = demoMode;
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
            CardFragment cardFragment = new CardFragment(decks.get(viewHolder.getAdapterPosition()).name);
            Bundle bundle = new Bundle();
            bundle.putBoolean("demoMode", demoMode);

            fragmentManager.executePendingTransactions();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment.isVisible()) {
                    transaction.hide(fragment);
                }
            }

            cardFragment.setArguments(bundle);
            transaction
                    .add(R.id.navHostFragmentActivityMain, cardFragment, CardFragment.TAG)
                    .addToBackStack(DeckFragment.TAG)
                    .setReorderingAllowed(true)
                    .commit();
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageBitmap(ImageHelper.toBitmap(decks.get(position).image));
        viewHolder.getTitleTextView().setText(decks.get(position).name);
        if (decks.get(position).description == null || decks.get(position).description.isEmpty()) {
            viewHolder.getDescriptionTextView().setVisibility(View.GONE);
        }
        else {
            viewHolder.getDescriptionTextView().setText(decks.get(position).description);
            viewHolder.getDescriptionTextView().setVisibility(View.VISIBLE);
        }

        viewHolder.getCardView().setChecked(selectionTracker.isSelected(decks.get(position).id));
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    public long getMaxId() {
        Optional<DeckModel> result = decks.stream().max(Comparator.comparingLong(deckModel -> deckModel.id));
        if (result.isPresent()) {
            return result.get().id;
        }
        else {
            return getItemCount();
        }
    }

    public DeckModel getItem(int position) {
        return decks.get(position);
    }

    public void addItem(DeckModel deckModel) {
        decks.add(0, deckModel);
        notifyItemInserted(0);
    }

    public void changeItem(int position, DeckModel newDeckModel) {
        decks.set(position, newDeckModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        decks.remove(position);
        notifyItemRemoved(position);
    }
}