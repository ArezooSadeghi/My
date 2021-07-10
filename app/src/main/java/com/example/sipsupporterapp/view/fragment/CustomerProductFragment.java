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
import com.example.sipsupporterapp.adapter.CustomerProductAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerProductBinding;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerProductDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeleteCustomerProductDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;

import java.util.Arrays;
import java.util.List;

public class CustomerProductFragment extends Fragment {
    private FragmentCustomerProductBinding binding;
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

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        fetchCustomerProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer_product,
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
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void initViews() {
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);

        binding.recyclerViewCustomerProduct.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.customer_divider_recycler_view));
        binding.recyclerViewCustomerProduct.addItemDecoration(dividerItemDecoration);

        binding.recyclerViewCustomerProduct.setHasFixedSize(true);
    }

    private void handleEvents() {
        binding.fabAddNewCustomerProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCustomerProductDialogFragment fragment = AddEditCustomerProductDialogFragment.newInstance(
                        customerID,
                        "",
                        0,
                        false,
                        false,
                        0, 0, 0);
                fragment.show(getParentFragmentManager(), AddEditCustomerProductDialogFragment.TAG);
            }
        });
    }

    private void setupAdapter(CustomerProductResult.CustomerProductInfo[] customerProductInfoArray) {
        List<CustomerProductResult.CustomerProductInfo> customerProductInfoList = Arrays.asList(customerProductInfoArray);
        CustomerProductAdapter adapter = new CustomerProductAdapter(getContext(), viewModel, customerProductInfoList);
        binding.recyclerViewCustomerProduct.setAdapter(adapter);
    }

    private void fetchCustomerProducts() {
        viewModel.getSipSupporterServiceCustomerProductsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/List/";
        viewModel.fetchCustomerProducts(path, userLoginKey, customerID);
    }

    private void deleteProduct() {
        viewModel.getSipSupporterServiceCustomerProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Delete/";
        viewModel.deleteCustomerProduct(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerProductID);
    }

    private void setupObserver() {
        viewModel.getCustomerProductsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult productResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (productResult.getErrorCode().equals("0")) {
                    binding.recyclerViewCustomerProduct.setVisibility(View.VISIBLE);

                    StringBuilder stringBuilder = new StringBuilder();
                    String listSize = String.valueOf(productResult.getCustomerProducts().length);

                    for (int i = 0; i < listSize.length(); i++) {
                        stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                    }

                    binding.txtCount.setText("تعداد محصولات: " + stringBuilder.toString());
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
            public void onChanged(Integer customer_ProductID) {
                customerProductID = customer_ProductID;
                QuestionDeleteCustomerProductDialogFragment fragment = QuestionDeleteCustomerProductDialogFragment.newInstance(getString(R.string.question_delete_customer_product_message));
                fragment.show(getActivity().getSupportFragmentManager(), QuestionDeleteCustomerProductDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDelete) {
                deleteProduct();
            }
        });

        viewModel.getDeleteCustomerProductResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_product));
                    fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCustomerProducts();
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
                AddEditCustomerProductDialogFragment fragment = AddEditCustomerProductDialogFragment.newInstance(
                        customerID,
                        customerProductInfo.getDescription(),
                        customerProductInfo.getInvoicePrice(),
                        customerProductInfo.isInvoicePayment(),
                        customerProductInfo.isFinish(),
                        customerProductInfo.getExpireDate(),
                        customerProductInfo.getCustomerProductID(), customerProductInfo.getProductID());
                fragment.show(getActivity().getSupportFragmentManager(), AddEditCustomerProductDialogFragment.TAG);
            }
        });

        viewModel.getDialogDismissed().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dialogDismissed) {
                fetchCustomerProducts();
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