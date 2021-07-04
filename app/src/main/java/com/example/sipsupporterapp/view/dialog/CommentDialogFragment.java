package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

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
import com.example.sipsupporterapp.adapter.CommentAdapter;
import com.example.sipsupporterapp.databinding.FragmentCommentDialogBinding;
import com.example.sipsupporterapp.model.CommentInfo;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommentDialogFragment extends DialogFragment {
    private FragmentCommentDialogBinding binding;
    private CommentViewModel viewModel;

    public static final String TAG = CommentDialogFragment.class.getSimpleName();

    public static CommentDialogFragment newInstance() {
        CommentDialogFragment fragment = new CommentDialogFragment();
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
                R.layout.fragment_comment_dialog,
                null,
                false);

        initViews();
        setupAdapter();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
    }

    private void setupObserver() {
        viewModel.getDeleteClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean deleteClicked) {
                QuestionDeleteCommentDialogFragment fragment = QuestionDeleteCommentDialogFragment.newInstance("آیا می خواهید نظر خود را حذف کنید");
                fragment.show(getParentFragmentManager(), QuestionDeleteCommentDialogFragment.TAG);
            }
        });

        viewModel.getEditClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean editClicked) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });
    }

    private void initViews() {
        binding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewComments.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewComments.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter() {
        List<CommentInfo> commentInfoList = new ArrayList<>();
        CommentAdapter adapter = new CommentAdapter(getContext(), viewModel, commentInfoList);
        binding.recyclerViewComments.setAdapter(adapter);
    }

    private void handleEvents() {
        binding.fabAddNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });
    }
}