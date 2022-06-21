package com.technophile.nuro.deck;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class DeckItemDetails extends ItemDetailsLookup.ItemDetails<Long> {

    private int position;
    private Long key;

    @Override
    public int getPosition() {
        return position;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return key;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSelectionKey(long key) {
        this.key = key;
    }
}
