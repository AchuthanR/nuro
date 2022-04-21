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
import com.capstone.autism_training.databinding.FragmentDeckBinding;
import com.capstone.autism_training.deck.DeckAdapter;
import com.capstone.autism_training.deck.DeckDetailsLookup;
import com.capstone.autism_training.deck.DeckInfoTableHelper;
import com.capstone.autism_training.deck.DeckInfoTableManager;
import com.capstone.autism_training.deck.DeckItemKeyProvider;
import com.capstone.autism_training.deck.DeckModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public class DeckFragment extends Fragment {

    public static final String TAG = DeckFragment.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    protected DeckAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public DeckInfoTableManager deckInfoTableManager;
    private SelectionTracker<Long> selectionTracker;

    private FragmentDeckBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDeckBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.extendedFAB.setOnClickListener(view1 -> {
            AddDeckDialogFragment addDeckDialogFragment = new AddDeckDialogFragment();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addDeckDialogFragment.show(transaction, AddDeckDialogFragment.TAG);
        });

        mRecyclerView = binding.recyclerView;
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new DeckAdapter(getParentFragmentManager());
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
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet_dialog);
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView deleteTextView = bottomSheetDialog.findViewById(R.id.action_delete);
                    if (deleteTextView != null) {
                        deleteTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete deck?")
                                    .setMessage("The selected deck and the flash cards inside it will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            deckInfoTableManager.deleteRow(id);
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Toast.makeText(getContext(), "Deleted the deck", Toast.LENGTH_LONG).show();
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

        deckInfoTableManager = new DeckInfoTableManager(getContext());
        deckInfoTableManager.open();
        Cursor cursor = deckInfoTableManager.fetch();

        int idIndex = cursor.getColumnIndex(DeckInfoTableHelper.ID);
        int imageIndex = cursor.getColumnIndex(DeckInfoTableHelper.IMAGE);
        int nameIndex = cursor.getColumnIndex(DeckInfoTableHelper.NAME);
        int descriptionIndex = cursor.getColumnIndex(DeckInfoTableHelper.DESCRIPTION);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            DeckModel deckModel = new DeckModel(cursor.getInt(idIndex), cursor.getBlob(imageIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex));
            mAdapter.addItem(deckModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        deckInfoTableManager.close();
    }
}