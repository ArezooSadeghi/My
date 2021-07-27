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
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
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
import java.util.List;

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
        fetchCustomerPayments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.base_layout,
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
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.ivMore.setVisibility(View.VISIBLE);

        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        binding.recyclerView.setHasFixedSize(true);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(getContext())
                        .addItem(new PowerMenuItem("افزودن واریزی جدید"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(12)
                        .setIconSize(24)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                AddEditCustomerPaymentDialogFragment fragment =
                                        AddEditCustomerPaymentDialogFragment.newInstance(
                                                "",
                                                0,
                                                0,
                                                customerID,
                                                0, 0, true);
                                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList = Arrays.asList(customerPaymentInfoArray);
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(getContext(), viewModel, customerPaymentInfoList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchCustomerPayments() {
        CustomerPaymentFragmentArgs args = CustomerPaymentFragmentArgs.fromBundle(getArguments());
        int customer_ID = args.getCustomerID();
        String path = "/api/v1/customerPayments/ListByCustomer/";
        if (customer_ID != 0) {
            viewModel.fetchCustomerPaymentsResult(path, userLoginKey, customer_ID);
        } else {
            viewModel.fetchCustomerPaymentsResult(path, userLoginKey, customerID);
        }
    }

    private void deleteCustomerPayment() {
        String path = "/api/v1/customerPayments/Delete/";
        viewModel.deleteCustomerPayment(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentID);
    }

    private void setupObserver() {
        viewModel.getCustomerPaymentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (customerPaymentResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    StringBuilder stringBuilder = new StringBuilder();
                    String listSize = String.valueOf(customerPaymentResult.getCustomerPayments().length);

                    for (int i = 0; i < listSize.length(); i++) {
                        stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                    }

                    binding.txtCount.setText("تعداد مبالغ واریزی: " + stringBuilder.toString());

                    setupAdapter(customerPaymentResult.getCustomerPayments());
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getSeeCustomerPaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, customerPaymentInfo.getCustomerPaymentID(), 0);
                startActivity(starter);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
                customerPaymentID = customerPaymentInfo.getCustomerPaymentID();
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.question_delete_customer_payment_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCustomerPaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_payment_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCustomerPayments();
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerPaymentResult.CustomerPaymentInfo>() {
            @Override
            public void onChanged(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
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
}