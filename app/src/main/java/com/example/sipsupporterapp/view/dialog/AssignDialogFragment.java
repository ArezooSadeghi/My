package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.AssignAdapter;
import com.example.sipsupporterapp.databinding.FragmentAssignDialogBinding;
import com.example.sipsupporterapp.model.AssignInfo;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;

import java.util.ArrayList;
import java.util.List;

public class AssignDialogFragment extends DialogFragment {
    private FragmentAssignDialogBinding binding;
    private AssignViewModel viewModel;

    public static final String TAG = AssignDialogFragment.class.getSimpleName();

    public static AssignDialogFragment newInstance() {
        AssignDialogFragment fragment = new AssignDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_assign_dialog,
                null,
                false);

        initViews();
        setupAdapter();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(AssignViewModel.class);
    }

    private void setupObserver() {
        viewModel.getEditClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean editClicked) {
                //TODO:show dialog
            }
        });

        viewModel.getDeleteClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean deleteClicked) {
                //TODO:show dialog
            }
        });

        viewModel.getRegisterCommentClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean registerCommentClicked) {
                //TODO
            }
        });
    }

    private void initViews() {
        binding.recyclerViewAssigns.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAssigns.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewAssigns.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter() {
        List<AssignInfo> assignInfoList = new ArrayList<>();
        AssignAdapter adapter = new AssignAdapter(getContext(), viewModel, assignInfoList);
        binding.recyclerViewAssigns.setAdapter(adapter);
    }
}