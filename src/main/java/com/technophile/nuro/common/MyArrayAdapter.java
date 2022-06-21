package com.technophile.nuro.common;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<String> {

    private final Filter noOpFilter;

    public MyArrayAdapter(Context context, int resource) {
        super(context, resource);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    public MyArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    public MyArrayAdapter(Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    public MyArrayAdapter(Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    public MyArrayAdapter(Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    public MyArrayAdapter(Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);

        noOpFilter = new Filter() {
            private final FilterResults noOpResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return this.noOpResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return this.noOpFilter;
    }
}
