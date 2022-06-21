package com.technophile.nuro.help;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class HelpCardDetailsLookup extends ItemDetailsLookup<Long> {

    private final RecyclerView recyclerView;

    public HelpCardDetailsLookup(RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof HelpCardAdapter.ViewHolder) {
                return ((HelpCardAdapter.ViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
