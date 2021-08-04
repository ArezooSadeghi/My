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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CommentAdapter;
import com.example.sipsupporterapp.databinding.FragmentCommentDialogBinding;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class CommentDialogFragment extends DialogFragment {
    private FragmentCommentDialogBinding binding;
    private CommentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int caseID, commentID;

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

        createViewModel();
        setupObserver();
        initVariables();
        fetchComments(caseID);
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
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getDeleteEvent(YesDeleteEvent event) {
        deleteComment(commentID);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
    }

    private void initVariables() {
        caseID = getArguments().getInt(ARGS_CASE_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(
                binding.recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
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

    private void fetchComments(int caseID) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Comment/list_ByCaseID/";
        viewModel.fetchCommentsByCaseID(path, userLoginKey, caseID);
    }

    private void deleteComment(int commentID) {
        viewModel.getSipSupporterServiceCommentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/comment/Delete/";
        viewModel.deleteComment(path, userLoginKey, commentID);
    }

    private void setupAdapter(CommentResult.CommentInfo[] commentInfoArray) {
        binding.txtEmpty.setVisibility(commentInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        CommentAdapter adapter = new CommentAdapter(getContext(), viewModel, Arrays.asList(commentInfoArray));
        binding.recyclerView.setAdapter(adapter);
    }

    private void handleEvents() {
        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance(0, caseID);
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setupObserver() {
        viewModel.getDeleteClicked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer comment_ID) {
                commentID = comment_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_question_comment_dialog));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCommentResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                if (commentResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_comment_message));
                    fetchComments(caseID);
                } else if (commentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(commentResult.getError());
                }
            }
        });

        viewModel.getEditClicked().observe(this, new Observer<CommentResult.CommentInfo>() {
            @Override
            public void onChanged(CommentResult.CommentInfo commentInfo) {
                AddEditCommentDialogFragment fragment = AddEditCommentDialogFragment.newInstance(commentInfo.getCommentID(), 0);
                fragment.show(getParentFragmentManager(), AddEditCommentDialogFragment.TAG);
            }
        });

        viewModel.getCommentsByCaseResultSingleLiveEvent().observe(this, new Observer<CommentResult>() {
            @Override
            public void onChanged(CommentResult commentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (commentResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(commentResult.getComments());
                } else if (commentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(commentResult.getError());
                }
            }
        });

        viewModel.getRefresh().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshComments) {
                fetchComments(caseID);
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });
    }
}