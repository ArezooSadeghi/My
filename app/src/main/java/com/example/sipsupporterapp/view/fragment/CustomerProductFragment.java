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
import com.example.sipsupporterapp.adapter.CustomerProductAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerProductBinding;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerProducts;
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

    private void fetchCustomerProducts() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceGetCustomerProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/List/";
        viewModel.fetchProductResult(path, userLoginKey, customerID);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void initViews() {
        String customerName = Converter.convert(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);

        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProducts.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewProducts.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.fabAddNewProduct.setOnClickListener(new View.OnClickListener() {
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

    private void setupObserver() {
        viewModel.getCustomerProductsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
                    @Override
                    public void onChanged(CustomerProductResult productResult) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.recyclerViewProducts.setVisibility(View.VISIBLE);

                        StringBuilder stringBuilder = new StringBuilder();
                        String listSize = String.valueOf(productResult.getCustomerProducts().length);

                        for (int i = 0; i < listSize.length(); i++) {
                            stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                        }

                        binding.txtCountProducts.setText("تعداد محصولات: " + stringBuilder.toString());
                        setupAdapter(productResult.getCustomerProducts());
                    }
                });

        viewModel.getErrorCustomerProductsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getNoConnection().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isTimeOutExceptionHappen) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        ErrorDialogFragment fragment = ErrorDialogFragment
                                .newInstance(getString(R.string.timeout_exception_happen_message));
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getDangerousUserSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDangerousUser) {
                SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
                SipSupportSharedPreferences.setUserFullName(getContext(), null);
                SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                SipSupportSharedPreferences.setCustomerName(getContext(), null);
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewModel.getDeleteClicked()
                .observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer customer_ProductID) {
                        customerProductID = customer_ProductID;
                        QuestionDeleteCustomerProductDialogFragment fragment = QuestionDeleteCustomerProductDialogFragment.newInstance(getString(R.string.question_delete_customer_product_message));
                        fragment.show(getActivity().getSupportFragmentManager(), QuestionDeleteCustomerProductDialogFragment.TAG);
                    }
                });

        viewModel.getYesDelete()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean yesDelete) {
                        deleteProduct();
                    }
                });

        viewModel.getDeleteCustomerProductResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerProductResult>() {
                    @Override
                    public void onChanged(CustomerProductResult customerProductResult) {
                        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_customer_product));
                        fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                        fetchCustomerProducts();
                    }
                });

        viewModel.getErrorDeleteCustomerProductResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                        fragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getEditClicked()
                .observe(getViewLifecycleOwner(), new Observer<CustomerProducts>() {
                    @Override
                    public void onChanged(CustomerProducts customerProducts) {
                        AddEditCustomerProductDialogFragment fragment = AddEditCustomerProductDialogFragment.newInstance(
                                customerID,
                                customerProducts.getDescription(),
                                customerProducts.getInvoicePrice(),
                                customerProducts.isInvoicePayment(),
                                customerProducts.isFinish(),
                                customerProducts.getExpireDate(),
                                customerProducts.getCustomerProductID(), customerProducts.getProductID());
                        fragment.show(getActivity().getSupportFragmentManager(), AddEditCustomerProductDialogFragment.TAG);
                    }
                });

        viewModel.getProductsFragmentDialogDismissSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dialogDismissed) {
                fetchCustomerProducts();
            }
        });

        viewModel.getProductAdapterSeeDocumentsClickedSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerProducts>() {
                    @Override
                    public void onChanged(CustomerProducts customerProducts) {
                        Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, customerProducts.getCustomerProductID(), 0, 0);
                        startActivity(starter);
                    }
                });
    }

    private void deleteProduct() {
        ServerData serverData = viewModel.getServerData(SipSupportSharedPreferences.getCenterName(getContext()));
        viewModel.getSipSupportServiceForDeleteCustomerProduct(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Delete/";
        viewModel.deleteCustomerProduct(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerProductID);
    }

    private void setupAdapter(CustomerProducts[] customerProductsArray) {
        List<CustomerProducts> customerProductsList = Arrays.asList(customerProductsArray);
        CustomerProductAdapter adapter = new CustomerProductAdapter(getContext(), customerProductsList, viewModel);
        binding.recyclerViewProducts.setAdapter(adapter);
    }
}