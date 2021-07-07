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

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditCommentDialogBinding;
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;

public class AddEditCommentDialogFragment extends DialogFragment {
    private FragmentAddEditCommentDialogBinding binding;
    private CommentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    private static final String ARGS_CASE_ID = "caseID";
    private static final String ARGS_COMMENT_ID = "commentID";
    private static final String ARGS_COMMENT = "comment";
    public static final String TAG = AddEditCommentDialogFragment.class.getSimpleName();

    public static AddEditCommentDialogFragment newInstance(int caseID, int commentID, String comment) {
        AddEditCommentDialogFragment fragment = new AddEditCommentDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_CASE_ID, caseID);
        args.putInt(ARGS_COMMENT_ID, commentID);
        args.putString(ARGS_COMMENT, comment);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_comment_dialog,
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
        viewModel.getAddCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("نظر شما با موفقیت ثبت شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefreshComments().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(commentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    dismiss();
                }
            }
        });

        viewModel.getEditCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("نظر شما با موفقیت ثبت شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefreshComments().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(commentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });
    }

    private void initViews() {
        String comment = getArguments().getString(ARGS_COMMENT);
        binding.edTextComment.setText(comment);
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentResult.CommentInfo commentInfo = new CommentResult().new CommentInfo();
                commentInfo.setComment(binding.edTextComment.getText().toString());

                int caseID = getArguments().getInt(ARGS_CASE_ID);
                commentInfo.setCaseID(caseID);

                int commentID = getArguments().getInt(ARGS_COMMENT_ID);
                commentInfo.setCommentID(commentID);

                if (commentID == 0) {
                    addComment(commentInfo);
                } else {
                    editComment(commentInfo);
                }
            }
        });
    }

    private void addComment(CommentResult.CommentInfo commentInfo) {
        viewModel.getSipSupporterServiceAddComment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Comment/Add/";
        viewModel.addComment(path, userLoginKey, commentInfo);
    }

    private void editComment(CommentResult.CommentInfo commentInfo) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/comment/Edit/";
        viewModel.editComment(path, userLoginKey, commentInfo);
    }
}