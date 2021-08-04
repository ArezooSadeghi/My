package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;

public class AddEditCommentDialogFragment extends DialogFragment {
    private FragmentAddEditCommentDialogBinding binding;
    private CommentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private CommentResult.CommentInfo commentInfo;
    private int commentID, caseID;

    private static final String ARGS_COMMENT_ID = "commentID";
    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = AddEditCommentDialogFragment.class.getSimpleName();

    public static AddEditCommentDialogFragment newInstance(int commentID, int caseID) {
        AddEditCommentDialogFragment fragment = new AddEditCommentDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_COMMENT_ID, commentID);
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        setupObserver();

        if (commentID > 0) {
            fetchCommentInfo(commentID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_comment_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
    }

    private void initVariables() {
        commentID = getArguments().getInt(ARGS_COMMENT_ID);
        caseID = getArguments().getInt(ARGS_CASE_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.edTextComment.setText(commentInfo.getComment());
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void addComment(CommentResult.CommentInfo commentInfo) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Comment/Add/";
        viewModel.addComment(path, userLoginKey, commentInfo);
    }

    private void editComment(CommentResult.CommentInfo commentInfo) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/comment/Edit/";
        viewModel.editComment(path, userLoginKey, commentInfo);
    }

    private void fetchCommentInfo(int commentID) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/comment/Info/";
        viewModel.fetchCommentInfo(path, userLoginKey, commentID);
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
                if (commentInfo == null) {
                    commentInfo = new CommentResult().new CommentInfo();
                    commentInfo.setCaseID(caseID);
                }

                commentInfo.setComment(binding.edTextComment.getText().toString());

                if (commentID == 0) {
                    addComment(commentInfo);
                } else {
                    editComment(commentInfo);
                }
            }
        });
    }

    private void setupObserver() {
        viewModel.getAddCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_comment_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (commentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(commentResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getEditCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_comment_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (commentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(commentResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getCommentInfoResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    commentInfo = commentResult.getComments()[0];
                    initViews();
                }
            }
        });
    }
}