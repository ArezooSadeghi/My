package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.AssignAdapter;
import com.example.sipsupporterapp.databinding.FragmentAssignDialogBinding;
import com.example.sipsupporterapp.eventbus.newDeleteEvent;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class AssignDialogFragment extends DialogFragment {
    private FragmentAssignDialogBinding binding;
    private AssignViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int caseID, assignID;

    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = AssignDialogFragment.class.getSimpleName();

    public static AssignDialogFragment newInstance(int caseID) {
        AssignDialogFragment fragment = new AssignDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        fetchAssigns(caseID);
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
    public void getDeleteEvent(newDeleteEvent event) {
        deleteAssign(assignID);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AssignViewModel.class);
    }

    private void initVariables() {
        caseID = getArguments().getInt(ARGS_CASE_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void setupAdapter(AssignResult.AssignInfo[] assignInfoArray) {
        binding.txtEmpty.setVisibility(assignInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        AssignAdapter adapter = new AssignAdapter(viewModel, Arrays.asList(assignInfoArray));
        binding.recyclerView.setAdapter(adapter);
    }

    private void deleteAssign(int assignID) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Delete/";
        viewModel.deleteAssign(path, userLoginKey, assignID);
    }

    private void fetchAssigns(int caseID) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/List/";
        viewModel.fetchAssigns(path, userLoginKey, caseID);
    }

    private void handleEvents() {
        binding.btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(0, caseID);
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
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
        viewModel.getEditClicked().observe(this, new Observer<AssignResult.AssignInfo>() {
            @Override
            public void onChanged(AssignResult.AssignInfo assignInfo) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(assignInfo.getAssignID(), assignInfo.getCaseID());
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer assign_ID) {
                assignID = assign_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_question_assign_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_delete_assign_message));
                    fetchAssigns(caseID);
                    dismiss();
                } else if (assignResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getAssignsResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (assignResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(assignResult.getAssigns());
                } else if (assignResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                }
            }
        });

        viewModel.getRefresh().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshAssigns) {
                fetchAssigns(caseID);
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