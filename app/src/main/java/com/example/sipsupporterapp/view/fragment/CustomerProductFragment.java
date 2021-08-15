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
import com.example.sipsupporterapp.adapter.CustomerProductAdapter;
import com.example.sipsupporterapp.databinding.BaseLayoutBinding;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerProductDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class CustomerProductFragment extends Fragment {
    private BaseLayoutBinding binding;
    private CustomerProductViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int customerID, customerProductID;

    private static final String ARGS_CUSTOMER_ID = "customerID";

    public static CustomerProductFragment newInstance(int customerID) {
        CustomerProductFragment fragment = new CustomerProductFragment();
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
        fetchCustomerProducts(customerID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.base_layout,
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
    public void getDeleteEvent(YesDeleteEvent event) {
        deleteCustomerProduct(customerProductID);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void initVariables() {
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.ivMore.setVisibility(View.VISIBLE);
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
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

    private void setupAdapter(CustomerProductResult.CustomerProductInfo[] customerProductInfoArray) {
        binding.txtEmpty.setVisibility(customerProductInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        CustomerProductAdapter adapter = new CustomerProductAdapter(viewModel, Arrays.asList(customerProductInfoArray));
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchCustomerProducts(int customerID) {
        viewModel.getSipSupporterServiceCustomerProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/List/";
        viewModel.fetchCustomerProducts(path, userLoginKey, customerID);
    }

    private void deleteCustomerProduct(int customerProductID) {
        viewModel.getSipSupporterServiceCustomerProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Delete/";
        viewModel.deleteCustomerProduct(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerProductID);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(getContext())
                        .addItem(new PowerMenuItem("افزودن محصول جدید"))
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
                                AddEditCustomerProductDialogFragment fragment = AddEditCustomerProductDialogFragment.newInstance(0, customerID);
                                fragment.show(getParentFragmentManager(), AddEditCustomerProductDialogFragment.TAG);
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

    private void setupObserver() {
        viewModel.getCustomerProductsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult productResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (productResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    String count = Converter.numberConverter(String.valueOf(productResult.getCustomerProducts().length));
                    binding.txtCount.setText("تعداد محصولات: " + count);
                    setupAdapter(productResult.getCustomerProducts());
                } else if (productResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(productResult.getError());
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

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer customer_Product_ID) {
                customerProductID = customer_Product_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_question_customer_product_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteCustomerProductResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_delete_customer_product));
                    fetchCustomerProducts(customerID);
                } else if (customerProductResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerProductResult.getError());
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<CustomerProductResult.CustomerProductInfo>() {
            @Override
            public void onChanged(CustomerProductResult.CustomerProductInfo customerProductInfo) {
                AddEditCustomerProductDialogFragment fragment = AddEditCustomerProductDialogFragment.newInstance(customerProductInfo.getCustomerProductID(), customerProductInfo.getCustomerID());
                fragment.show(getActivity().getSupportFragmentManager(), AddEditCustomerProductDialogFragment.TAG);
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dialogDismissed) {
                fetchCustomerProducts(customerID);
            }
        });

        viewModel.getSeeCustomerProductAttachmentsClicked()
                .observe(getViewLifecycleOwner(), new Observer<CustomerProductResult.CustomerProductInfo>() {
                    @Override
                    public void onChanged(CustomerProductResult.CustomerProductInfo customerProductInfo) {
                        Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, customerProductInfo.getCustomerProductID(), 0, 0);
                        startActivity(starter);
                    }
                });
    }
}