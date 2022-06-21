package com.technophile.nuro.deck;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class DeckDetailsLookup extends ItemDetailsLookup<Long> {

    private final RecyclerView recyclerView;

    public DeckDetailsLookup(RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof DeckAdapter.ViewHolder) {
                return ((DeckAdapter.ViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
