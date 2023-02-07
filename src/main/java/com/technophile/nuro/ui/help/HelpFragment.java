package com.technophile.nuro.ui.help;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technophile.nuro.R;
import com.technophile.nuro.common.MyArrayAdapter;
import com.technophile.nuro.databinding.FragmentHelpBinding;
import com.technophile.nuro.help.HelpCardAdapter;
import com.technophile.nuro.help.HelpCardDetailsLookup;
import com.technophile.nuro.help.HelpCardItemKeyProvider;
import com.technophile.nuro.help.HelpCardModel;
import com.technophile.nuro.help.HelpTableHelper;
import com.technophile.nuro.help.HelpTableManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpFragment extends Fragment {

    public static final String TAG = HelpFragment.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    protected HelpCardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public HelpTableManager helpTableManager;
    private SelectionTracker<Long> selectionTracker;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private BottomSheetDialog bottomSheetDialog;
    private ArrayList<String> decks;

    private boolean demoMode = false;

    private FragmentHelpBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add && getChildFragmentManager().getFragments().isEmpty()) {
                AddHelpCardDialogFragment addHelpCardDialogFragment = new AddHelpCardDialogFragment(demoMode);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                addHelpCardDialogFragment.show(transaction, AddHelpCardDialogFragment.TAG);
                return true;
            }
            else if (item.getItemId() == R.id.action_help) {
                binding.chooseDeckAutoCompleteTextView.setText(getString(R.string.introduction_text_fragment_help), false);
                demoMode = true;
                deckSelected("Introduction");
                return true;
            }
            return false;
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        mRecyclerView = binding.recyclerView;
        if (dpWidth > 600) {
            mLayoutManager = new GridLayoutManager(getContext(), (int) (dpWidth / 400));
        }
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        mAdapter = new HelpCardAdapter(getParentFragmentManager());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "selectionId",
                mRecyclerView,
                new HelpCardItemKeyProvider(mRecyclerView),
                new HelpCardDetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
                .build();

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (!selectionTracker.getSelection().isEmpty() && getContext() != null) {
                    bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_dialog);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView editTextView = bottomSheetDialog.findViewById(R.id.action_edit);
                    if (editTextView != null) {
                        editTextView.setOnClickListener(view1 -> {
                            bottomSheetDialog.dismiss();
                            if (!selectionTracker.hasSelection()) {
                                return;
                            }
                            long id = selectionTracker.getSelection().iterator().next();
                            selectionTracker.clearSelection();
                            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(id);
                            if (viewHolder != null) {
                                HelpCardModel helpCardModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                EditHelpCardDialogFragment editHelpCardDialogFragment = new EditHelpCardDialogFragment(demoMode);
                                editHelpCardDialogFragment.setHelpCardModel(helpCardModel);
                                editHelpCardDialogFragment.setAdapterPosition(viewHolder.getAdapterPosition());

                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                editHelpCardDialogFragment.show(transaction, EditHelpCardDialogFragment.TAG);
                            }
                        });
                    }

                    MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                    if (deleteTextView != null) {
                        deleteTextView.setOnClickListener(view1 -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete help card?")
                                    .setMessage("The selected help card will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            if (!demoMode) {
                                                helpTableManager.deleteRow(id);
                                            }
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Snackbar.make(view, "Deleted the help card", Snackbar.LENGTH_LONG)
                                                    .setAction("OKAY", view2 -> {}).show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                    .setOnCancelListener(dialogInterface -> selectionTracker.clearSelection())
                                    .show();
                        });
                    }

                    MaterialTextView cancelTextView = bottomSheetDialog.findViewById(R.id.action_cancel);
                    if (cancelTextView != null) {
                        cancelTextView.setOnClickListener(view1 -> bottomSheetDialog.cancel());
                    }

                    bottomSheetDialog.show();
                }
            }
        });
        mAdapter.setSelectionTracker(selectionTracker);

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (binding.emptyDeckTextView.getVisibility() != View.GONE) {
                    binding.emptyDeckTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() == 0) {
                    binding.emptyDeckTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        mAdapter.registerAdapterDataObserver(adapterDataObserver);

        helpTableManager = new HelpTableManager(getContext());

        decks = new ArrayList<>(Arrays.asList("Requests", "Responses", "Emotions", "Problems"));
        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseDeckAutoCompleteTextView.setAdapter(adapter);

        binding.chooseDeckAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            demoMode = false;
            deckSelected(adapterView.getItemAtPosition(i).toString());
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!binding.chooseDeckAutoCompleteTextView.getText().toString().isEmpty()) {
            deckSelected(binding.chooseDeckAutoCompleteTextView.getText().toString());
        }
        else {
            binding.chooseDeckAutoCompleteTextView.setText(decks.get(0), false);
            deckSelected(decks.get(0));
        }
    }

    private void deckSelected(String deck) {
        mAdapter.clearAll();

        helpTableManager.close();
        helpTableManager.open(deck);
        Cursor cursor = helpTableManager.fetch();

        int idIndex = cursor.getColumnIndex(HelpTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(HelpTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(HelpTableHelper.IMAGE);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            HelpCardModel helpCardModel = new HelpCardModel(cursor.getLong(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex));
            mAdapter.addItem(helpCardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        helpTableManager.close();
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        }
    }
}