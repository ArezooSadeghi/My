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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CustomerPaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentNewCustomerPaymentsBinding;
import com.example.sipsupporterapp.model.BankAccountInfo;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeleteCustomerPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewCustomerPaymentsFragment extends Fragment {
    private FragmentNewCustomerPaymentsBinding binding;
    private CustomerPaymentViewModel viewModel;

    private String centerName, userLoginKey;
    private int bankAccountID, customerPaymentID;
    private ServerData serverData;
    private List<String> bankAccountNames = new ArrayList<>();
    private List<Integer> bankAccountIDs = new ArrayList<>();

    public static NewCustomerPaymentsFragment newInstance() {
        NewCustomerPaymentsFragment fragment = new NewCustomerPaymentsFragment();
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

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_customer_payments,
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void fetchBankAccounts() {
        viewModel.getSipSupporterServiceBankAccounts(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/BankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void initViews() {
        binding.recyclerViewPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPayments.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewPayments.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.spinnerBankAccounts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String lastValueSpinner = (String) item;
                for (int i = 0; i < bankAccountNames.size(); i++) {
                    if (bankAccountNames.get(i).equals(lastValueSpinner)) {
                        bankAccountID = bankAccountIDs.get(i);
                        break;
                    }
                }
                fetchCustomerPaymentsByBankAccount(bankAccountID);
            }
        });

        binding.fabAddNewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance("", 0, 0, 0, 0, bankAccountID, false);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult.getErrorCode().equals("0")) {
                    setupSpinner(bankAccountResult.getBankAccounts());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(bankAccountResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getCustomerPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);

                if (customerPaymentResult.getErrorCode().equals("0")) {
                    binding.recyclerViewPayments.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    setupAdapter(customerPaymentResult.getCustomerPayments());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerPaymentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo info) {
                customerPaymentID = info.getCustomerPaymentID();
                QuestionDeleteCustomerPaymentDialogFragment fragment = QuestionDeleteCustomerPaymentDialogFragment.newInstance(getString(R.string.delete_new_customer_payments_question_message));
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean okDeleteClicked) {
                viewModel.getSipSupporterServiceDeleteCustomerPayment(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/customerPayments/Delete/";
                viewModel.deleteCustomerPayment(path, userLoginKey, customerPaymentID);
            }
        });

        viewModel.getDeleteCustomerPaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    fetchCustomerPaymentsByBankAccount(bankAccountID);
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_payments_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerPaymentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo info) {
                int customerSupportID = 0;
                int customerProductID = 0;
                int paymentID = 0;
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), customerSupportID, customerProductID, info.getCustomerPaymentID(), paymentID);
                startActivity(starter);
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo info) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(info.getDescription(), info.getPrice(), info.getDatePayment(), info.getCustomerID(), info.getCustomerPaymentID(), info.getBankAccountID(), false);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });

        viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean updateList) {
                fetchCustomerPaymentsByBankAccount(bankAccountID);
            }
        });
    }

    private void setupSpinner(BankAccountInfo[] bankAccountInfoArray) {
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNames.add(i, bankAccountInfoArray[i].getBankAccountName());
            bankAccountIDs.add(i, bankAccountInfoArray[i].getBankAccountID());
        }
        binding.spinnerBankAccounts.setItems(bankAccountNames);
        bankAccountID = bankAccountIDs.get(0);
        fetchCustomerPaymentsByBankAccount(bankAccountID);
    }

    private void fetchCustomerPaymentsByBankAccount(int bankAccountID) {
        binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.GONE ? View.VISIBLE : View.VISIBLE);
        viewModel.getSipSupporterServiceCustomerPaymentsByBankAccount(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/CustomerPayments/ListByBankAccount/";
        viewModel.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    private void setupAdapter(CustomerPaymentInfo[] customerPaymentInfoArray) {
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(getContext(), viewModel, Arrays.asList(customerPaymentInfoArray));
        binding.recyclerViewPayments.setAdapter(adapter);
    }
}