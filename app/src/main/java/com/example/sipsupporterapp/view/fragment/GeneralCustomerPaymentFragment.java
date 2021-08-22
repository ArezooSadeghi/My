package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CustomerPaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentGeneralCustomerPaymentBinding;
import com.example.sipsupporterapp.eventbus.PostBankAccountResultEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeneralCustomerPaymentFragment extends Fragment {
    private FragmentGeneralCustomerPaymentBinding binding;
    private CustomerPaymentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private List<BankAccountResult.BankAccountInfo> bankAccountInfoList;
    private int bankAccountID;

    public static GeneralCustomerPaymentFragment newInstance() {
        GeneralCustomerPaymentFragment fragment = new GeneralCustomerPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
        fetchBankAccounts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general_customer_payment, container, false);
        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void initVariables() {
        bankAccountInfoList = new ArrayList<>();
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void fetchBankAccounts() {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void handleEvents() {
        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                bankAccountID = bankAccountInfoList.get(position).getBankAccountID();
                fetchCustomerPaymentsByBankAccount(bankAccountID);
            }
        });
    }

    private void fetchCustomerPaymentsByBankAccount(int bankAccountID) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "api/v1/customerPayments/ListByBankAccount";
        viewModel.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (Objects.requireNonNull(bankAccountResult).getErrorCode().equals("0")) {
                    EventBus.getDefault().postSticky(new PostBankAccountResultEvent(bankAccountResult));
                    bankAccountInfoList = Arrays.asList(bankAccountResult.getBankAccounts());
                    setupSpinner(bankAccountResult.getBankAccounts());
                } else if (Objects.requireNonNull(bankAccountResult).getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(Objects.requireNonNull(bankAccountResult).getError());
                }
            }
        });

        viewModel.getCustomerPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (Objects.requireNonNull(customerPaymentResult).getErrorCode().equals("0")) {
                    if (customerPaymentResult.getCustomerPayments().length == 0) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.txtEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.txtEmpty.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        viewModel.getCustomerPaymentsResultSingleLiveEvent().setValue(customerPaymentResult);
                        setupAdapter(customerPaymentResult.getCustomerPayments());
                    }
                } else if (Objects.requireNonNull(customerPaymentResult).getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(Objects.requireNonNull(customerPaymentResult).getError());
                }
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, customerPaymentInfo.getCustomerPaymentID(), 0);
                startActivity(starter);
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, customerPaymentInfo.getCustomerPaymentID(), 0);
                startActivity(starter);
            }
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        List<String> bankAccountNameList = new ArrayList<>();
        for (BankAccountResult.BankAccountInfo bankAccountInfo : bankAccountInfoArray) {
            bankAccountNameList.add(Converter.letterConverter(bankAccountInfo.getBankAccountName()));
        }
        bankAccountID = bankAccountInfoArray[0].getBankAccountID();
        fetchCustomerPaymentsByBankAccount(bankAccountID);
        binding.spinner.setItems(bankAccountNameList);
    }

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(viewModel, Arrays.asList(customerPaymentInfoArray), true);
        binding.recyclerView.setAdapter(adapter);
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
}