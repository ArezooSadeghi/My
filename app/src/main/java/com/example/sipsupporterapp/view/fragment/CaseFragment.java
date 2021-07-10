package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CaseAdapter;
import com.example.sipsupporterapp.databinding.FragmentCaseBinding;
import com.example.sipsupporterapp.eventbus.PostCustomerIDEvent;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCaseDialogFragment;
import com.example.sipsupporterapp.view.dialog.AssignDialogFragment;
import com.example.sipsupporterapp.view.dialog.ChangeCaseTypeDialogFragment;
import com.example.sipsupporterapp.view.dialog.CommentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeleteTaskDialogFragment;
import com.example.sipsupporterapp.view.dialog.RegisterCaseResultDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.TaskViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseFragment extends Fragment {
    private FragmentCaseBinding binding;
    private TaskViewModel viewModel;
    private ServerData serverData;
    private int caseTypeID, customerID, caseID;
    private String centerName, userLoginKey;
    private List<String> caseTypes = new ArrayList<>();
    private List<Integer> caseTypeIDs = new ArrayList<>();

    public static CaseFragment newInstance() {
        CaseFragment fragment = new CaseFragment();
        Bundle args = new Bundle();
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

        fetchCaseTypes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_case,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
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

    @Subscribe(sticky = true)
    public void getCustomerID(PostCustomerIDEvent event) {
        customerID = event.getCustomerID();
        String customerName = event.getCustomerName();
        AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, customerID, customerName, 0, false, "");
        fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    }

    private void fetchCaseTypes() {
        viewModel.getSipSupporterServiceCaseTypes(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/caseType/List/";
        viewModel.fetchCaseTypes(path, userLoginKey);
    }

    private void initViews() {
        binding.recyclerViewCases.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCases.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCases.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.spinnerCaseTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String caseType = (String) item;
                for (int i = 0; i < caseTypes.size(); i++) {
                    if (caseType.equals(caseTypes.get(i))) {
                        caseTypeID = caseTypeIDs.get(i);
                        SipSupportSharedPreferences.setCaseTypeID(getContext(), caseTypeID);
                        fetchCasesByCaseType(caseTypeIDs.get(i), "تست", true);
                        break;
                    }
                }
            }
        });

        binding.fabAddNewCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, 0, "", 0, false, "");
                fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
            }
        });
    }

    private void setupObserver() {
        viewModel.getCaseTypesResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseTypeResult>() {
            @Override
            public void onChanged(CaseTypeResult caseTypeResult) {
                if (caseTypeResult.getErrorCode().equals("0")) {
                    setupSpinner(caseTypeResult.getCaseTypes());
                    CaseFragmentArgs args = CaseFragmentArgs.fromBundle(getArguments());
                    customerID = args.getCustomerID();
                    if (customerID != 0) {
                        int caseTypeID = SipSupportSharedPreferences.getCaseTypeID(getContext());
                        AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, customerID, "", 0, false, "");
                        fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
                    }
                } else if (caseTypeResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseTypeResult.getError());
                }
            }
        });

        viewModel.getCasesByCaseTypeResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                    binding.recyclerViewCases.setVisibility(View.VISIBLE);
                    setupAdapter(caseResult.getCases());
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                    handleError(caseResult.getError());
                }
            }
        });

        viewModel.getCaseFinishClicked().observe(getViewLifecycleOwner(), new Observer<CaseResult.CaseInfo>() {
            @Override
            public void onChanged(CaseResult.CaseInfo caseInfo) {
                RegisterCaseResultDialogFragment fragment = RegisterCaseResultDialogFragment.newInstance(caseInfo.getCaseID(), caseInfo.isResultOk(), caseInfo.getResultDescription());
                fragment.show(getParentFragmentManager(), RegisterCaseResultDialogFragment.TAG);
            }
        });

        viewModel.getChangeCaseTypeClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changeCaseTypeClicked) {
                ChangeCaseTypeDialogFragment fragment = ChangeCaseTypeDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), ChangeCaseTypeDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer case_ID) {
                caseID = case_ID;
                QuestionDeleteTaskDialogFragment fragment = QuestionDeleteTaskDialogFragment.newInstance("آیا می خواهید کار مورد نظر را حذف کنید؟");
                fragment.show(getParentFragmentManager(), QuestionDeleteTaskDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDeleteClicked) {
                viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/Case/Delete/";
                viewModel.deleteCase(path, userLoginKey, caseID);
            }
        });

        viewModel.getDeleteCaseResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("حذف کار با موفقیت انجام شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCasesByCaseType(caseTypeID, "تست", true);
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CaseResult.CaseInfo>() {
            @Override
            public void onChanged(CaseResult.CaseInfo caseInfo) {
                AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(
                        caseInfo.getCaseID(),
                        caseInfo.getCaseTypeID(),
                        caseInfo.getCustomerID(),
                        caseInfo.getCustomerName(),
                        caseInfo.getPriority(),
                        caseInfo.isShare(),
                        caseInfo.getDescription());
                fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refresh) {
                fetchCasesByCaseType(caseTypeID, "تست", true);
            }
        });

        viewModel.getRegisterCommentClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                CommentDialogFragment fragment = CommentDialogFragment.newInstance(caseID);
                fragment.show(getParentFragmentManager(), CommentDialogFragment.TAG);
            }
        });

        viewModel.getRefreshCaseFinishClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshCaseFinish) {
                fetchCasesByCaseType(caseTypeID, "تست", true);
            }
        });

        viewModel.getAssignToOthersClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                AssignDialogFragment fragment = AssignDialogFragment.newInstance(caseID);
                fragment.show(getParentFragmentManager(), AssignDialogFragment.TAG);
            }
        });

        viewModel.getCaseProductsClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                CaseFragmentDirections.ActionMenuTasksToCaseProductsFragment action = CaseFragmentDirections.actionMenuTasksToCaseProductsFragment();
                action.setCaseID(caseID);
                NavHostFragment.findNavController(CaseFragment.this).navigate(action);
            }
        });

        viewModel.getNavigateToCustomerFragment().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean navigateToCustomerFragment) {
                SipSupportSharedPreferences.setCaseTypeID(getContext(), caseTypeID);
                CaseFragmentDirections.ActionMenuTasksToMenuSearch action =
                        CaseFragmentDirections.actionMenuTasksToMenuSearch();
                action.setIsCase(true);
                NavHostFragment.findNavController(CaseFragment.this).navigate(action);
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    private void setupSpinner(CaseTypeResult.CaseTypeInfo[] caseTypeInfoArray) {
        for (int i = 0; i < caseTypeInfoArray.length; i++) {
            caseTypes.add(i, caseTypeInfoArray[i].getCaseType());
            caseTypeIDs.add(i, caseTypeInfoArray[i].getCaseTypeID());
        }

        binding.spinnerCaseTypes.setItems(caseTypes);
        caseTypeID = caseTypeIDs.get(0);
        SipSupportSharedPreferences.setCaseTypeID(getContext(), caseTypeID);
        fetchCasesByCaseType(caseTypeIDs.get(0), "تست", true);
    }

    private void fetchCasesByCaseType(int caseTypeID, String search, boolean showAll) {
        viewModel.getSipSupporterServiceCasesByCaseType(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/List/";
        viewModel.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, showAll);
    }

    private void setupAdapter(CaseResult.CaseInfo[] caseInfoArray) {
        CaseAdapter adapter = new CaseAdapter(getContext(), viewModel, Arrays.asList(caseInfoArray));
        binding.recyclerViewCases.setAdapter(adapter);
    }
}