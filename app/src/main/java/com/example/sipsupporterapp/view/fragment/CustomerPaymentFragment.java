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
import com.example.sipsupporterapp.databinding.FragmentCustomerPaymentBinding;
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

import java.util.Arrays;
import java.util.List;

public class CustomerPaymentFragment extends Fragment {
    private FragmentCustomerPaymentBinding binding;
    private CustomerPaymentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int customerID, customerPaymentID;

    private static final String ARGS_CUSTOMER_ID = "customerID";

    public static CustomerPaymentFragment newInstance(int customerID) {
        CustomerPaymentFragment fragment = new CustomerPaymentFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        createViewModel();
        fetchCustomerPayments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer_payment,
                null,
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

    private void initViews() {
        binding.recyclerViewDepositAmounts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDepositAmounts.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewDepositAmounts.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.fabAddNewCustomerPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditCustomerPaymentDialogFragment fragment =
                        AddEditCustomerPaymentDialogFragment.newInstance(
                                "",
                                0,
                                0,
                                customerID,
                                0, 0, true);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });
    }

    private void setupAdapter(CustomerPaymentInfo[] customerPaymentInfoArray) {
        List<CustomerPaymentInfo> customerPaymentInfoList = Arrays.asList(customerPaymentInfoArray);
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(getContext(), viewModel, customerPaymentInfoList);
        binding.recyclerViewDepositAmounts.setAdapter(adapter);
    }

    private void fetchCustomerPayments() {
        viewModel.getSipSupporterServiceCustomerPaymentsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/ListByCustomer/";
        viewModel.fetchCustomerPaymentsResult(path, userLoginKey, customerID);
    }

    private void deleteCustomerPayment() {
        viewModel.getSipSupporterServiceDeleteCustomerPayment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Delete/";
        viewModel.deleteCustomerPayment(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentID);
    }

    private void setupObserver() {
        viewModel.getCustomerPaymentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (customerPaymentResult.getErrorCode().equals("0")) {
                    binding.recyclerViewDepositAmounts.setVisibility(View.VISIBLE);
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
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo customerPaymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, customerPaymentInfo.getCustomerPaymentID(), 0);
                startActivity(starter);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo customerPaymentInfo) {
                customerPaymentID = customerPaymentInfo.getCustomerPaymentID();
                QuestionDeleteCustomerPaymentDialogFragment fragment = QuestionDeleteCustomerPaymentDialogFragment.newInstance(getString(R.string.question_delete_customer_payment_message));
                fragment.show(getParentFragmentManager(), QuestionDeleteCustomerPaymentDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDeleteCustomerPayment) {
                deleteCustomerPayment();
            }
        });

        viewModel.getDeleteCustomerPaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_payment_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCustomerPayments();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerPaymentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentInfo customerPaymentInfo) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment
                        .newInstance(
                                customerPaymentInfo.getDescription(),
                                customerPaymentInfo.getPrice(),
                                customerPaymentInfo.getDatePayment(),
                                customerID,
                                customerPaymentInfo.getCustomerPaymentID(), customerPaymentInfo.getBankAccountID(), true);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });

        viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                fetchCustomerPayments();
            }
        });
    }
}