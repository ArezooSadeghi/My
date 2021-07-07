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
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;

import java.util.Arrays;

public class CommentDialogFragment extends DialogFragment {
    private FragmentCommentDialogBinding binding;
    private CommentViewModel viewModel;
    private int caseID, commentID;
    private ServerData serverData;
    private String centerName, userLoginKey;

    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = CommentDialogFragment.class.getSimpleName();

    public static CommentDialogFragment newInstance(int caseID) {
        CommentDialogFragment fragment = new CommentDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        caseID = getArguments().getInt(ARGS_CASE_ID);

        createViewModel();
        setupObserver();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        fetchComments();
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
        viewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
    }

    private void setupObserver() {
        viewModel.getDeleteClicked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer comment_ID) {
                commentID = comment_ID;
                QuestionDeleteCommentDialogFragment fragment = QuestionDeleteCommentDialogFragment.newInstance("آیا می خواهید نظر خود را حذف کنید");
                fragment.show(getParentFragmentManager(), QuestionDeleteCommentDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDeleteClicked) {
                deleteComment();
            }
        });

        viewModel.getDeleteCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("نظر شما با موفقیت حذف شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchComments();

                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(commentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getEditClicked().observe(this, new Observer<CommentResult.CommentInfo>() {
            @Override
            public void onChanged(CommentResult.CommentInfo commentInfo) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance(commentInfo.getCaseID(), commentInfo.getCommentID(), commentInfo.getComment());
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });

        viewModel.getCommentsByCaseIDResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    setupAdapter(commentResult.getComments());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(commentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getRefreshComments().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshComments) {
                fetchComments();
            }
        });
    }

    private void fetchComments() {
        viewModel.getSipSupporterServiceCommentsByCaseID(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Comment/list_ByCaseID/";
        viewModel.fetchCommentsByCaseID(path, userLoginKey, caseID);
    }

    private void deleteComment() {
        viewModel.getSipSupporterServiceDeleteComment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/comment/Delete/";
        viewModel.deleteComment(path, userLoginKey, commentID);
    }

    private void initViews() {
        binding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewComments.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewComments.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter(CommentResult.CommentInfo[] commentInfoArray) {
        if (commentInfoArray.length != 0) {
            binding.txtNoComment.setVisibility(View.INVISIBLE);
            binding.recyclerViewComments.setVisibility(View.VISIBLE);
            CommentAdapter adapter = new CommentAdapter(getContext(), viewModel, Arrays.asList(commentInfoArray));
            binding.recyclerViewComments.setAdapter(adapter);
        }
    }

    private void handleEvents() {
        binding.fabAddNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance(caseID, 0, "");
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });
    }
}