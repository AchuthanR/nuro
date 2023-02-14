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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.technophile.nuro.R;
import com.technophile.nuro.card.CardAdapter;
import com.technophile.nuro.card.CardDetailsLookup;
import com.technophile.nuro.card.CardItemKeyProvider;
import com.technophile.nuro.card.CardModel;
import com.technophile.nuro.card.DeckTableHelper;
import com.technophile.nuro.card.DeckTableManager;
import com.technophile.nuro.databinding.FragmentCardBinding;

public class CardFragment extends Fragment {

    public static final String TAG = CardFragment.class.getSimpleName();

    private String TABLE_NAME = "";

    protected RecyclerView mRecyclerView;
    protected CardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public DeckTableManager deckTableManager;
    private SelectionTracker<Long> selectionTracker;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private BottomSheetDialog bottomSheetDialog;

    private boolean demoMode = false;
    private boolean readOnlyMode = false;

    private FragmentCardBinding binding;

    public CardFragment() {

    }

    public CardFragment(String table_name) {
        this.TABLE_NAME = table_name;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCardBinding.inflate(inflater, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("TABLE_NAME")) {
            TABLE_NAME = savedInstanceState.getString("TABLE_NAME");
        }
        binding.toolbar.setTitle(TABLE_NAME);

        if (getArguments() != null) {
            if (getArguments().containsKey("demoMode")) {
                demoMode = getArguments().getBoolean("demoMode");
            }

            if (getArguments().containsKey("readOnlyMode")) {
                readOnlyMode = getArguments().getBoolean("readOnlyMode");
                if (readOnlyMode) {
                    binding.toolbar.getMenu().removeItem(R.id.action_add);
                }
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add && getChildFragmentManager().getFragments().isEmpty()) {
                AddCardDialogFragment addCardDialogFragment = new AddCardDialogFragment(demoMode);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                addCardDialogFragment.show(transaction, AddCardDialogFragment.TAG);
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
        mAdapter = new CardAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (!readOnlyMode) {
            selectionTracker = new SelectionTracker.Builder<>(
                    "selectionId",
                    mRecyclerView,
                    new CardItemKeyProvider(mRecyclerView),
                    new CardDetailsLookup(mRecyclerView),
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
                                    CardModel cardModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                    EditCardDialogFragment editCardDialogFragment = new EditCardDialogFragment(demoMode);
                                    editCardDialogFragment.setCardModel(cardModel);
                                    editCardDialogFragment.setAdapterPosition(viewHolder.getAdapterPosition());

                                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    editCardDialogFragment.show(transaction, EditCardDialogFragment.TAG);
                                }
                            });
                        }

                        MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                        if (deleteTextView != null) {
                            deleteTextView.setOnClickListener(view1 -> {
                                bottomSheetDialog.dismiss();
                                new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                        .setIcon(R.drawable.ic_round_delete_24)
                                        .setTitle("Delete card?")
                                        .setMessage("The selected card will be deleted permanently.")
                                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                                            if (selectionTracker.hasSelection()) {
                                                long id = selectionTracker.getSelection().iterator().next();
                                                selectionTracker.clearSelection();
                                                if (!demoMode) {
                                                    deckTableManager.deleteRow(id);
                                                }
                                                mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                                Snackbar.make(view, "Deleted the card", Snackbar.LENGTH_LONG)
                                                        .setAction("OKAY", view2 -> {
                                                        }).show();
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
        }

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

        deckTableManager = new DeckTableManager(getContext());
        fetchFromTable();
    }

    private void fetchFromTable() {
        deckTableManager.close();
        mAdapter.clearAll();
        deckTableManager.open(TABLE_NAME);
        Cursor cursor = deckTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int shortAnswerIndex = cursor.getColumnIndex(DeckTableHelper.SHORT_ANSWER);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getLong(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(shortAnswerIndex));
            mAdapter.addItem(cardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TABLE_NAME", TABLE_NAME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        deckTableManager.close();
        mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        }
    }
}