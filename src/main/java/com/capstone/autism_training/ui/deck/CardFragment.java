package com.capstone.autism_training.ui.deck;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.card.CardAdapter;
import com.capstone.autism_training.card.CardDetailsLookup;
import com.capstone.autism_training.card.CardItemKeyProvider;
import com.capstone.autism_training.card.CardModel;
import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.databinding.FragmentCardBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class CardFragment extends Fragment {

    public static final String TAG = CardFragment.class.getSimpleName();

    private String TABLE_NAME = "";
    private final ArrayList<String> tableNameBackStack = new ArrayList<>();

    protected RecyclerView mRecyclerView;
    protected CardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public DeckTableManager deckTableManager;
    private SelectionTracker<Long> selectionTracker;

    private FragmentCardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCardBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            TABLE_NAME = getArguments().getString("TABLE_NAME").replace(" ", "_");
            binding.toolbarLayout.setTitle(getArguments().getString("TABLE_NAME") + " deck");
            tableNameBackStack.add(getArguments().getString("TABLE_NAME"));
            setArguments(null);
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

        binding.extendedFAB.setOnClickListener(view1 -> {
            AddCardDialogFragment addCardDialogFragment = new AddCardDialogFragment();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addCardDialogFragment.show(transaction, AddCardDialogFragment.TAG);
        });

        mRecyclerView = binding.recyclerView;
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CardAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet_dialog);
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                    if (deleteTextView != null) {
                        deleteTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete card?")
                                    .setMessage("The selected card will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            deckTableManager.deleteRow(id);
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Toast.makeText(getContext(), "Deleted the card", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                    .setOnCancelListener(dialogInterface -> selectionTracker.clearSelection())
                                    .show();
                        });
                    }

                    MaterialTextView cancelTextView = bottomSheetDialog.findViewById(R.id.action_cancel);
                    if (cancelTextView != null) {
                        cancelTextView.setOnClickListener(view -> bottomSheetDialog.cancel());
                    }

                    bottomSheetDialog.show();
                }
            }
        });
        mAdapter.setSelectionTracker(selectionTracker);

        deckTableManager = new DeckTableManager(getContext());
        fetchFromTable();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getArguments() != null) {
                TABLE_NAME = getArguments().getString("TABLE_NAME").replace(" ", "_");
                binding.toolbarLayout.setTitle(getArguments().getString("TABLE_NAME") + " deck");
                tableNameBackStack.add(getArguments().getString("TABLE_NAME"));
                setArguments(null);
            }
            else {
                String table_name = tableNameBackStack.remove(tableNameBackStack.size() - 1);
                TABLE_NAME = table_name.replace(" ", "_");
                binding.toolbarLayout.setTitle(table_name + " deck");
            }

            fetchFromTable();
        }
    }

    private void fetchFromTable() {
        deckTableManager.close();
        mAdapter.clearAll();
        deckTableManager.open(TABLE_NAME);
        Cursor cursor = deckTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckTableHelper.IMAGE);
        int captionIndex = cursor.getColumnIndex(DeckTableHelper.CAPTION);
        int answerIndex = cursor.getColumnIndex(DeckTableHelper.ANSWER);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            CardModel cardModel = new CardModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(captionIndex), cursor.getString(answerIndex));
            mAdapter.addItem(cardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        deckTableManager.close();
    }
}