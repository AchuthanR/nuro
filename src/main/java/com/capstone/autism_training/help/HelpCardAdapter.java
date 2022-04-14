package com.capstone.autism_training.help;

import android.content.Context;
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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class HelpCardAdapter extends RecyclerView.Adapter<HelpCardAdapter.ViewHolder> {

    private final ArrayList<HelpCardModel> helpCards;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final TextView nameTextView;
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

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            helpCardItemDetails.setPosition(getAdapterPosition());
            helpCardItemDetails.setSelectionKey(helpCards.get(getAdapterPosition()).id);
            return helpCardItemDetails;
        }

        public final void bind(boolean isActive) {
            cardView.setChecked(isActive);
        }
    }

    public HelpCardAdapter() {
        helpCards = new ArrayList<>();
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(helpCards.get(position).image, 0, helpCards.get(position).image.length, options1);

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
        viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(helpCards.get(position).image, 0, helpCards.get(position).image.length, options2));
        viewHolder.getNameTextView().setText(helpCards.get(position).name);

        viewHolder.bind(selectionTracker.isSelected(helpCards.get(position).id));
    }

    @Override
    public int getItemCount() {
        return helpCards.size();
    }

    public void addItem(HelpCardModel helpCardModel) {
        helpCards.add(0, helpCardModel);
        notifyItemInserted(0);
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