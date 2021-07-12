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
import com.example.sipsupporterapp.adapter.AssignAdapter;
import com.example.sipsupporterapp.databinding.FragmentAssignDialogBinding;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
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
    private int caseID, assignID;
    private ServerData serverData;
    private String centerName, userLoginKey;

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

        caseID = getArguments().getInt(ARGS_CASE_ID);

        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());

        fetchAssigns();
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
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AssignViewModel.class);
    }

    private void fetchAssigns() {
        String path = "/api/v1/Assign/List/";
        viewModel.fetchAssigns(path, userLoginKey, caseID);
    }

    private void setupObserver() {
        viewModel.getEditClicked().observe(this, new Observer<AssignResult.AssignInfo>() {
            @Override
            public void onChanged(AssignResult.AssignInfo assignInfo) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(assignInfo.getAssignID(), assignInfo.getCaseID(), assignInfo.getDescription());
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer assign_ID) {
                assignID = assign_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance("آیا می خواهید assign موردنظر را حذف نمایید؟");
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("assign موردنظر با موفقیت حذف شد");
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    fetchAssigns();
                    dismiss();
                } else if (assignResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getRegisterCommentClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean registerCommentClicked) {
                //TODO
            }
        });

        viewModel.getAssignsResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    setupAdapter(assignResult.getAssigns());
                } else if (assignResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                }
            }
        });

        viewModel.getRefreshAssigns().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshAssigns) {
                fetchAssigns();
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

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void initViews() {
        binding.recyclerViewAssigns.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAssigns.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewAssigns.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.fabAddNewAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(0, caseID, "");
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
            }
        });
    }

    private void setupAdapter(AssignResult.AssignInfo[] assignInfoArray) {
        if (assignInfoArray.length != 0) {
            binding.txtNoAssign.setVisibility(View.INVISIBLE);
            binding.recyclerViewAssigns.setVisibility(View.VISIBLE);
            AssignAdapter adapter = new AssignAdapter(getContext(), viewModel, Arrays.asList(assignInfoArray));
            binding.recyclerViewAssigns.setAdapter(adapter);
        }
    }

    private void deleteAssign() {
        String path = "/api/v1/Assign/Delete/";
        viewModel.deleteAssign(path, userLoginKey, assignID);
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
    public void getYesDeleteEvent(YesDeleteEvent event) {
        deleteAssign();
    }
}