package com.capstone.autism_training.ui.help;

import android.content.res.Configuration;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.common.MyArrayAdapter;
import com.capstone.autism_training.databinding.FragmentHelpBinding;
import com.capstone.autism_training.help.HelpCardAdapter;
import com.capstone.autism_training.help.HelpCardDetailsLookup;
import com.capstone.autism_training.help.HelpCardItemKeyProvider;
import com.capstone.autism_training.help.HelpCardModel;
import com.capstone.autism_training.help.HelpCardTableHelper;
import com.capstone.autism_training.help.HelpCardTableManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpFragment extends Fragment {

    public static final String TAG = HelpFragment.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    protected HelpCardAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public HelpCardTableManager helpCardTableManager;
    private SelectionTracker<Long> selectionTracker;
    private ArrayList<String> decks;

    private FragmentHelpBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.extendedFAB.setOnClickListener(view1 -> {
            AddHelpCardDialogFragment addHelpCardDialogFragment = new AddHelpCardDialogFragment();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            addHelpCardDialogFragment.show(transaction, AddHelpCardDialogFragment.TAG);
        });

        mRecyclerView = binding.recyclerView;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(getContext(), 2);
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
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet_dialog);
                    bottomSheetDialog.setOnCancelListener(dialogInterface -> selectionTracker.clearSelection());

                    MaterialTextView editTextView = bottomSheetDialog.findViewById(R.id.action_edit);
                    if (editTextView != null) {
                        editTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            if (!selectionTracker.hasSelection()) {
                                return;
                            }
                            long id = selectionTracker.getSelection().iterator().next();
                            selectionTracker.clearSelection();
                            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(id);
                            if (viewHolder != null) {
                                HelpCardModel helpCardModel = mAdapter.getItem(viewHolder.getAdapterPosition());

                                EditHelpCardDialogFragment editHelpCardDialogFragment = new EditHelpCardDialogFragment();
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
                        deleteTextView.setOnClickListener(view -> {
                            bottomSheetDialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setIcon(R.drawable.ic_round_delete_24)
                                    .setTitle("Delete help card?")
                                    .setMessage("The selected help card will be deleted permanently.")
                                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                                        if (selectionTracker.hasSelection()) {
                                            long id = selectionTracker.getSelection().iterator().next();
                                            selectionTracker.clearSelection();
                                            helpCardTableManager.deleteRow(id);
                                            mAdapter.removeItem(mRecyclerView.findViewHolderForItemId(id).getAdapterPosition());
                                            Toast.makeText(getContext(), "Deleted the help card", Toast.LENGTH_LONG).show();
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

        helpCardTableManager = new HelpCardTableManager(getContext());

        decks = new ArrayList<>(Arrays.asList("Requests", "Responses", "Emotions", "Problems"));
        MyArrayAdapter adapter = new MyArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, decks);
        binding.chooseCategoryAutoCompleteTextView.setAdapter(adapter);

        binding.chooseCategoryAutoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> categorySelected(adapterView.getItemAtPosition(i).toString()));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!binding.chooseCategoryAutoCompleteTextView.getText().toString().isEmpty()) {
            categorySelected(binding.chooseCategoryAutoCompleteTextView.getText().toString());
        }
        else {
            binding.chooseCategoryAutoCompleteTextView.setText(decks.get(0), false);
            categorySelected(decks.get(0));
        }
    }

    private void categorySelected(String deck) {
        mAdapter.clearAll();

        helpCardTableManager.close();
        helpCardTableManager.open(deck);
        Cursor cursor = helpCardTableManager.fetch();

        int idIndex = cursor.getColumnIndex(HelpCardTableHelper.ID);
        int nameIndex = cursor.getColumnIndex(HelpCardTableHelper.NAME);
        int imageIndex = cursor.getColumnIndex(HelpCardTableHelper.IMAGE);
        while (!cursor.isAfterLast() || cursor.isFirst()) {
            HelpCardModel helpCardModel = new HelpCardModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getBlob(imageIndex));
            mAdapter.addItem(helpCardModel);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        helpCardTableManager.close();
    }
}