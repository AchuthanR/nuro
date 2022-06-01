package com.capstone.autism_training.help;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.ui.help.ActiveHelpCardDialogFragment;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class HelpCardAdapter extends RecyclerView.Adapter<HelpCardAdapter.ViewHolder> {

    private final ArrayList<HelpCardModel> helpCards;
    private final FragmentManager fragmentManager;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ShapeableImageView imageView;
        private final MaterialTextView nameTextView;
        private final HelpCardItemDetails helpCardItemDetails;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            nameTextView = view.findViewById(R.id.nameTextView);
            helpCardItemDetails = new HelpCardItemDetails();
        }

        public Context getContext() {
            return context;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ShapeableImageView getImageView() {
            return imageView;
        }

        public MaterialTextView getNameTextView() {
            return nameTextView;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            helpCardItemDetails.setPosition(getAdapterPosition());
            helpCardItemDetails.setSelectionKey(helpCards.get(getAdapterPosition()).id);
            return helpCardItemDetails;
        }
    }

    public HelpCardAdapter(FragmentManager fragmentManager) {
        helpCards = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return helpCards.get(position).id;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_help_card_item, viewGroup, false);

        HelpCardAdapter.ViewHolder viewHolder = new HelpCardAdapter.ViewHolder(view);

        viewHolder.getCardView().setOnClickListener(view1 -> {
            ActiveHelpCardDialogFragment activeHelpCardDialogFragment = new ActiveHelpCardDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putByteArray("image", helpCards.get(viewHolder.getAdapterPosition()).image);
            activeHelpCardDialogFragment.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            activeHelpCardDialogFragment.show(transaction, ActiveHelpCardDialogFragment.TAG);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageBitmap(ImageHelper.toCompressedBitmap(helpCards.get(position).image));
        viewHolder.getNameTextView().setText(helpCards.get(position).name);

        viewHolder.getCardView().setChecked(selectionTracker.isSelected(helpCards.get(position).id));
    }

    @Override
    public int getItemCount() {
        return helpCards.size();
    }

    public HelpCardModel getItem(int position) {
        return helpCards.get(position);
    }

    public void addItem(HelpCardModel helpCardModel) {
        helpCards.add(0, helpCardModel);
        notifyItemInserted(0);
    }

    public void changeItem(int position, HelpCardModel newHelpCardModel) {
        helpCards.set(position, newHelpCardModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        helpCards.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAll() {
        int size = helpCards.size();
        helpCards.clear();
        notifyItemRangeRemoved(0, size);
    }
}