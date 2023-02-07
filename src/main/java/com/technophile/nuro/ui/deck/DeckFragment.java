package com.technophile.nuro.ui.deck;

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
import com.technophile.nuro.databinding.FragmentDeckBinding;
import com.technophile.nuro.deck.DeckAdapter;
import com.technophile.nuro.deck.DeckDetailsLookup;
import com.technophile.nuro.deck.CollectionTableHelper;
import com.technophile.nuro.deck.CollectionTableManager;
import com.technophile.nuro.deck.DeckItemKeyProvider;
import com.technophile.nuro.deck.DeckModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

public class DeckFragment extends Fragment {

    public static final String TAG = DeckFragment.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    protected DeckAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public CollectionTableManager collectionTableManager;
    private SelectionTracker<Long> selectionTracker;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private BottomSheetDialog bottomSheetDialog;

    private boolean demoMode = false;

    private FragmentDeckBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDeckBinding.inflate(inflater, container, false);

        if (getArguments() != null && getArguments().containsKey("demoMode") && getArguments().getBoolean("demoMode")) {
            demoMode = true;
            binding.toolbar.getMenu().removeItem(R.id.action_help);
            binding.logoButton.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (demoMode) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24);
            binding.toolbar.setNavigationOnClickListener(view1 -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_add && getChildFragmentManager().getFragments().isEmpty()) {
                    AddDeckDialogFragment addDeckDialogFragment = new AddDeckDialogFragment(demoMode);

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    addDeckDialogFragment.show(transaction, AddDeckDialogFragment.TAG);
                    return true;
                }
                return false;
            });
        }
        else {
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_add && getChildFragmentManager().getFragments().isEmpty()) {
                    AddDeckDialogFragment addDeckDialogFragment = new AddDeckDialogFragment(demoMode);

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    addDeckDialogFragment.show(transaction, AddDeckDialogFragment.TAG);
                    return true;
                }
                else if (item.getItemId() == R.id.action_help) {
                    DeckFragment deckFragment = new DeckFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("demoMode", true);
                    deckFragment.setArguments(bundle);

                    getParentFragmentManager().executePendingTransactions();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    for (Fragment fragment : getParentFragmentManager().getFragments()) {
                        if (fragment.isVisible()) {
                            transaction.hide(fragment);
                        }
                    }

                    transaction
                            .add(R.id.navHostFragmentActivityMain, deckFragment, DeckFragment.TAG)
                            .addToBackStack(DeckFragment.TAG)
                            .setReorderingAllowed(true);
                    transaction.commit();
                    return true;
                }
                return false;
            });
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        mRecyclerView = binding.recyclerView;
        if (dpWidth > 600) {
            mLayoutManager = new GridLayoutManager(getContext(), (int) (dpWidth / 400));
        }
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        mAdapter = new DeckAdapter(getParentFragmentManager(), demoMode);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "selectionId",
                mRecyclerView,
                new DeckItemKeyProvider(mRecyclerView),
                new DeckDetailsLookup(mRecyclerView),
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
                                DeckModel deckModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                EditDeckDialogFragment editDeckDialogFragment = new EditDeckDialogFragment(demoMode);
                                editDeckDialogFragment.setDeckModel(deckModel);
                                editDeckDialogFragment.setAdapterPosition(viewHolder.getAdapterPosition());

                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                editDeckDialogFragment.show(transaction, EditDeckDialogFragment.TAG);
                            }
                        });
                    }

                    MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                    if (deleteTextView != null) {
                        deleteTextView.setOnClickListener(view1 -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete deck?")
                                    .setMessage("The selected deck and the flash cards inside it will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            int position = mRecyclerView.findViewHolderForItemId(id).getAdapterPosition();
                                            if (!demoMode) {
                                                collectionTableManager.deleteRow(id, mAdapter.getItem(position).name);
                                            }
                                            mAdapter.removeItem(position);
                                            Snackbar.make(view, "Deleted the deck", Snackbar.LENGTH_LONG)
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
                if (binding.zeroDeckTextView.getVisibility() != View.GONE) {
                    binding.zeroDeckTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() == 0) {
                    binding.zeroDeckTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        mAdapter.registerAdapterDataObserver(adapterDataObserver);

        collectionTableManager = new CollectionTableManager(getContext());
        if (demoMode) {
            collectionTableManager.open("Introduction");
        }
        else {
            collectionTableManager.open();
        }
        Cursor cursor = collectionTableManager.fetch();

        int idIndex = cursor.getColumnIndex(CollectionTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(CollectionTableHelper.IMAGE);
        int nameIndex = cursor.getColumnIndex(CollectionTableHelper.NAME);
        int descriptionIndex = cursor.getColumnIndex(CollectionTableHelper.DESCRIPTION);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            DeckModel deckModel = new DeckModel(cursor.getLong(idIndex), cursor.getBlob(imageIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex));
            mAdapter.addItem(deckModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        collectionTableManager.close();
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        }
    }
}