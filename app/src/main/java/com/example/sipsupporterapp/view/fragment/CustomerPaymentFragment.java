package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.sipsupporterapp.databinding.BaseLayoutBinding;
import com.example.sipsupporterapp.eventbus.newDeleteEvent;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class CustomerPaymentFragment extends Fragment {
    private BaseLayoutBinding binding;
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
        createViewModel();
        initVariables();
        fetchCustomerPayments(customerID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.base_layout, null, false);

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
    public void getDeleteEvent(newDeleteEvent event) {
        deleteCustomerPayment(customerPaymentID);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void initVariables() {
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void fetchCustomerPayments(int customerID) {
        String path = "/api/v1/customerPayments/ListByCustomer/";
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchCustomerPaymentsResult(path, userLoginKey, customerID);
    }

    private void initViews() {
        binding.ivMore.setVisibility(View.VISIBLE);
        binding.txtCustomerName.setText(Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext())));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void handleEvents() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerMenu powerMenu = new PowerMenu.Builder(getContext())
                        .addItem(new PowerMenuItem(getString(R.string.power_menu_add_payment_item_title)))
                        .setTextColor(Color.BLACK)
                        .setTextSize(12)
                        .setIconSize(24)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(0, customerID, 0);
                                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(binding.ivMore);
            }
        });
    }

    private void setupObserver() {
        viewModel.getCustomerPaymentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (customerPaymentResult != null) {
                    if (customerPaymentResult.getErrorCode().equals("0")) {
                        String count = Converter.numberConverter(String.valueOf(customerPaymentResult.getCustomerPayments().length));
                        binding.txtCount.setText("تعداد مبالغ واریزی: " + count);
                        if (customerPaymentResult.getCustomerPayments().length == 0) {
                            binding.txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            binding.txtEmpty.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            setupAdapter(customerPaymentResult.getCustomerPayments());
                        }
                    } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(customerPaymentResult.getError());
                    }
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            handleError(msg);
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, customerPaymentInfo.getCustomerPaymentID(), 0);
                startActivity(starter);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer customer_Payment_ID) {
                customerPaymentID = customer_Payment_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_question_customer_payment_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCustomerPaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult != null) {
                    if (customerPaymentResult.getErrorCode().equals("0")) {
                        showSuccessDialog("واریزی با موفقیت حذف شد");
                        fetchCustomerPayments(customerID);
                    } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(customerPaymentResult.getError());
                    }
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(customerPaymentInfo.getCustomerPaymentID(), customerID, 0);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refresh) {
                fetchCustomerPayments(customerID);
            }
        });
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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
        SipSupportSharedPreferences.setCaseID(getContext(), 0);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(viewModel, Arrays.asList(customerPaymentInfoArray), false);
        binding.recyclerView.setAdapter(adapter);
    }

    private void deleteCustomerPayment(int customerPaymentID) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Delete/";
        viewModel.deleteCustomerPayment(path, userLoginKey, customerPaymentID);
    }
}