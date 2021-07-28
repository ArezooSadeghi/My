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
import com.example.sipsupporterapp.databinding.FragmentNewCustomerPaymentsBinding;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewCustomerPaymentsFragment extends Fragment {
    private FragmentNewCustomerPaymentsBinding binding;
    private CustomerPaymentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int bankAccountID, customerPaymentID;
    private List<String> bankAccountNames;
    private List<Integer> bankAccountIDs;

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
        deleteCustomerPayment();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void initVariables() {
        bankAccountNames = new ArrayList<>();
        bankAccountIDs = new ArrayList<>();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerViewNewCustomerPayment.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerViewNewCustomerPayment.addItemDecoration(dividerItemDecoration);
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
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(0, 0, bankAccountID, 0, 0, "", 0);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNames.add(i, bankAccountInfoArray[i].getBankAccountName());
            bankAccountIDs.add(i, bankAccountInfoArray[i].getBankAccountID());
        }
        binding.spinnerBankAccounts.setItems(bankAccountNames);
        bankAccountID = bankAccountIDs.get(0);
        fetchCustomerPaymentsByBankAccount(bankAccountID);
    }

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(getContext(), viewModel, Arrays.asList(customerPaymentInfoArray));
        binding.recyclerViewNewCustomerPayment.setAdapter(adapter);
    }

    private void fetchCustomerPaymentsByBankAccount(int bankAccountID) {
        binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.GONE ? View.VISIBLE : View.VISIBLE);
        String path = "/api/v1/CustomerPayments/ListByBankAccount/";
        viewModel.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    private void fetchBankAccounts() {
        String path = "/api/v1/BankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult.getErrorCode().equals("0")) {
                    setupSpinner(bankAccountResult.getBankAccounts());
                } else if (bankAccountResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(bankAccountResult.getError());
                }
            }
        });

        viewModel.getCustomerPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);

                if (customerPaymentResult.getErrorCode().equals("0")) {
                    binding.recyclerViewNewCustomerPayment.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    setupAdapter(customerPaymentResult.getCustomerPayments());
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(binding.progressBarLoading.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
                handleError(message);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo info) {
                customerPaymentID = info.getCustomerPaymentID();
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_new_customer_payments_question_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCustomerPaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    fetchCustomerPaymentsByBankAccount(bankAccountID);
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_payments_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo info) {
                int customerSupportID = 0;
                int customerProductID = 0;
                int paymentID = 0;
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), customerSupportID, customerProductID, info.getCustomerPaymentID(), paymentID);
                startActivity(starter);
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(customerPaymentInfo.getCustomerPaymentID(), customerPaymentInfo.getCustomerID(), customerPaymentInfo.getBankAccountID(), customerPaymentInfo.getDatePayment(), customerPaymentInfo.getPrice(), customerPaymentInfo.getDescription(), customerPaymentInfo.getCaseID());
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

    private void deleteCustomerPayment() {
        String path = "/api/v1/customerPayments/Delete/";
        viewModel.deleteCustomerPayment(path, userLoginKey, customerPaymentID);
    }
}