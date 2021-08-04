package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

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
import com.example.sipsupporterapp.eventbus.CaseTypesEvent;
import com.example.sipsupporterapp.eventbus.PostCustomerIDEvent;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCaseDialogFragment;
import com.example.sipsupporterapp.view.dialog.AssignDialogFragment;
import com.example.sipsupporterapp.view.dialog.ChangeCaseTypeDialogFragment;
import com.example.sipsupporterapp.view.dialog.CommentDialogFragment;
import com.example.sipsupporterapp.view.dialog.CustomerPaymentByCaseDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.RegisterCaseResultDialogFragment;
import com.example.sipsupporterapp.view.dialog.SeeDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseFragment extends Fragment {
    private FragmentCaseBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey, searchQuery;
    private CaseResult.CaseInfo caseInfo;
    private List<String> caseTypes;
    private List<Integer> caseTypeIDs;
    private AddEditCaseDialogFragment fragment;
    private int caseTypeID, customerID, caseID;

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
        initVariables();
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
        AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, customerID);
        fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe
    public void getYesDeleteEvent(YesDeleteEvent event) {
        try {
            int newCaseID = Integer.valueOf(caseID);
            deleteCase(newCaseID);
        } catch (NumberFormatException e) {
        }
    }

    private void deleteCase(int caseID) {
        if (caseID != 0) {
            String path = "/api/v1/Case/Delete/";
            viewModel.deleteCase(path, userLoginKey, caseID);
        }
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CaseViewModel.class);
    }

    private void initVariables() {
        caseTypes = new ArrayList<>();
        caseTypeIDs = new ArrayList<>();

        searchQuery = "";

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCaseTypeResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceCasesByCaseType(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerViewCases.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCases.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCases.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void fetchCaseTypes() {
        String path = "/api/v1/caseType/List/";
        viewModel.fetchCaseTypes(path, userLoginKey);
    }

    private void handleEvents() {
        binding.spinnerCaseTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String caseType = Converter.letterConverter((String) item);
                for (int i = 0; i < caseTypes.size(); i++) {
                    if (caseType.equals(caseTypes.get(i))) {
                        caseTypeID = caseTypeIDs.get(i);
                        SipSupportSharedPreferences.setCaseTypeID(getContext(), caseTypeID);
                        fetchCasesByCaseType(caseTypeIDs.get(i), "", binding.checkBoxShowAllCases.isChecked());
                        break;
                    }
                }
            }
        });

        binding.checkBoxShowAllCases.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fetchCasesByCaseType(caseTypeID, searchQuery, isChecked);
            }
        });
    }

    private void setupObserver() {
        viewModel.getCaseTypesResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseTypeResult>() {
            @Override
            public void onChanged(CaseTypeResult caseTypeResult) {
                if (caseTypeID == 0) {
                    if (caseTypeResult.getErrorCode().equals("0")) {
                        EventBus.getDefault().postSticky(new CaseTypesEvent(caseTypeResult));
                        setupSpinner(caseTypeResult.getCaseTypes());
                        CaseFragmentArgs args = CaseFragmentArgs.fromBundle(getArguments());
                        customerID = args.getCustomerID();
                    } else if (caseTypeResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(caseTypeResult.getError());
                    }
                } else {
                    EventBus.getDefault().post(new CaseTypesEvent(caseTypeResult));
                }
            }
        });

        viewModel.getCasesByCaseTypeResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    binding.progressBarLoading.setVisibility(View.GONE);
                    if (caseResult.getCases().length == 0) {
                        binding.txtNoResult.setVisibility(View.VISIBLE);
                        binding.recyclerViewCases.setVisibility(View.GONE);
                    } else {
                        binding.txtNoResult.setVisibility(View.GONE);
                        binding.recyclerViewCases.setVisibility(View.VISIBLE);
                        setupAdapter(caseResult.getCases());
                    }
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    binding.progressBarLoading.setVisibility(View.GONE);
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

        viewModel.getChangeCaseTypeClicked().observe(getViewLifecycleOwner(), new Observer<CaseResult.CaseInfo>() {
            @Override
            public void onChanged(CaseResult.CaseInfo case_Info) {
                caseInfo = case_Info;
                ChangeCaseTypeDialogFragment fragment = ChangeCaseTypeDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), ChangeCaseTypeDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer case_ID) {
                caseID = case_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance("آیا می خواهید کار مورد نظر را حذف کنید؟");
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCaseResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("حذف کار با موفقیت انجام شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());
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
                fragment = AddEditCaseDialogFragment.newInstance(
                        caseInfo.getCaseID(),
                        caseInfo.getCaseTypeID(),
                        caseInfo.getCustomerID());
                fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refresh) {
                fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());
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
                fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());
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

        viewModel.getMoreClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean moreClicked) {
                CaseFragmentDirections.ActionMenuTasksToMenuSearch action = CaseFragmentDirections.actionMenuTasksToMenuSearch();
                action.setFlag(true);
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

        viewModel.getPrintInvoiceClicked().observe(getViewLifecycleOwner(), new Observer<CaseResult.CaseInfo>() {
            @Override
            public void onChanged(CaseResult.CaseInfo caseInfo) {
                CaseFragmentDirections.ActionMenuTasksToInvoiceFragment action = CaseFragmentDirections.actionMenuTasksToInvoiceFragment();
                action.setCaseID(caseInfo.getCaseID());
                action.setCustomerID(caseInfo.getCustomerID());
                action.setCustomerName(caseInfo.getCustomerName());
                NavHostFragment.findNavController(CaseFragment.this).navigate(action);
            }
        });

        viewModel.getPaymentListClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer customerID) {
                CaseFragmentDirections.ActionMenuTasksToCustomerPaymentFragment action = CaseFragmentDirections.actionMenuTasksToCustomerPaymentFragment();
                action.setCustomerID(customerID);
                NavHostFragment.findNavController(CaseFragment.this).navigate(action);
            }
        });

        viewModel.getRefreshSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer case_TypeID) {
                caseTypeID = case_TypeID;
                fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());
                for (int i = 0; i < caseTypeIDs.size(); i++) {
                    if (caseTypeIDs.get(i) == caseTypeID) {
                        String caseType = caseTypes.get(i);
                        caseTypes.remove(caseType);
                        caseTypes.add(0, caseType);
                        binding.spinnerCaseTypes.setItems(caseTypes);
                    }
                }
            }
        });

        viewModel.getCaseSearchQuery().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String search) {
                searchQuery = search;
                String path = "/api/v1/Case/List/";
                viewModel.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, binding.checkBoxShowAllCases.isChecked());
            }
        });

        viewModel.getAddNewCaseClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean addNewCaseClicked) {
                AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, 0);
                fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
            }
        });

        viewModel.getSeenClicked().observe(getViewLifecycleOwner(), new Observer<AssignResult.AssignInfo>() {
            @Override
            public void onChanged(AssignResult.AssignInfo assignInfo) {
                seen(assignInfo);
            }
        });

        viewModel.getFinishClicked().observe(getViewLifecycleOwner(), new Observer<AssignResult.AssignInfo>() {
            @Override
            public void onChanged(AssignResult.AssignInfo assignInfo) {
                finish(assignInfo);
            }
        });

        viewModel.getFinishAssignResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {

            }
        });

        viewModel.getSeenAssignResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {

            }
        });

        viewModel.getEditCaseResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment successFragment = SuccessDialogFragment.newInstance(getString(R.string.success_add_edit_case_message));
                    successFragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());
                    if (fragment.getShowsDialog()) {
                        fragment.dismiss();
                    }
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                }
            }
        });

        viewModel.getSaveClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseTypeID) {
                editCase(caseTypeID);
            }
        });

        viewModel.getAddPaymentClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                CustomerPaymentByCaseDialogFragment fragment = CustomerPaymentByCaseDialogFragment.newInstance(caseID);
                fragment.show(getParentFragmentManager(), CustomerPaymentByCaseDialogFragment.TAG);
            }
        });

        viewModel.getSeeCommentClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                CommentDialogFragment fragment = CommentDialogFragment.newInstance(caseID);
                fragment.show(getParentFragmentManager(), CommentDialogFragment.TAG);
            }
        });

        viewModel.getSeeClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer caseID) {
                SeeDialogFragment fragment = SeeDialogFragment.newInstance(caseID);
                fragment.show(getParentFragmentManager(), SeeDialogFragment.TAG);
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
            caseTypes.add(i, Converter.letterConverter(caseTypeInfoArray[i].getCaseType()));
            caseTypeIDs.add(i, caseTypeInfoArray[i].getCaseTypeID());
        }

        caseTypeID = caseTypeIDs.get(0);
        binding.spinnerCaseTypes.setItems(caseTypes);

        SipSupportSharedPreferences.setCaseTypeID(getContext(), caseTypeID);
        fetchCasesByCaseType(caseTypeID, searchQuery, binding.checkBoxShowAllCases.isChecked());

        CaseFragmentArgs args = CaseFragmentArgs.fromBundle(getArguments());
        int customerID = args.getCustomerID();
        if (customerID != 0) {
            AddEditCaseDialogFragment fragment = AddEditCaseDialogFragment.newInstance(0, caseTypeID, customerID);
            fragment.show(getParentFragmentManager(), AddEditCaseDialogFragment.TAG);
        }
    }

    private void fetchCasesByCaseType(int caseTypeID, String search, boolean showAll) {
        String path = "/api/v1/Case/List/";
        viewModel.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, showAll);
    }

    private void setupAdapter(CaseResult.CaseInfo[] caseInfoArray) {
        CaseAdapter adapter = new CaseAdapter(getContext(), viewModel, Arrays.asList(caseInfoArray));
        binding.recyclerViewCases.setAdapter(adapter);
    }

    private void seen(AssignResult.AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Seen/";
        viewModel.seen(path, userLoginKey, assignInfo);
    }

    private void finish(AssignResult.AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Finish/";
        viewModel.finish(path, userLoginKey, assignInfo);
    }

    private void editCase(int caseTypeID) {
        String path = "/api/v1/Case/Edit/";
        caseInfo.setCaseTypeID(caseTypeID);
        viewModel.editCase(path, userLoginKey, caseInfo);
    }
}