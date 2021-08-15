package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditCustomerProductDialogBinding;
import com.example.sipsupporterapp.eventbus.PostProductGroupIDEvent;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.ProductsContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditCustomerProductDialogFragment extends DialogFragment {
    private FragmentAddEditCustomerProductDialogBinding binding;
    private CustomerProductViewModel viewModel;
    private ServerData serverData;
    private String currentDate, centerName, userLoginKey;
    private CustomerProductResult.CustomerProductInfo customerProductInfo;
    private ProductResult.ProductInfo productInfo;
    private int customerProductID, customerID, currentYear, currentMonth, currentDay, productGroupID;

    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String ARGS_CUSTOMER_ID = "customerID";
    public static final String TAG = AddEditCustomerProductDialogFragment.class.getSimpleName();

    public static AddEditCustomerProductDialogFragment newInstance(int customerProductID, int customerID) {
        AddEditCustomerProductDialogFragment fragment = new AddEditCustomerProductDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_PRODUCT_ID, customerProductID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        setupObserver();

        if (customerProductID > 0) {
            fetchCustomerProductInfo(customerProductID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_customer_product_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
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
    public void getProductGroupID(PostProductGroupIDEvent event) {
        productGroupID = event.getProductGroupID();
        fetchProductInfo(productGroupID);
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void initVariables() {
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        if (customerProductInfo != null) {
            String customerName = Converter.letterConverter(customerProductInfo.getCustomerName());
            binding.txtCustomerName.setText(customerName);
            binding.edTextDescription.setText(customerProductInfo.getDescription());
            binding.edTextDescription.setSelection(binding.edTextDescription.getText().toString().length());
            binding.btnProductName.setText(customerProductInfo.getProductName());
            binding.edTextInvoicePrice.setText(String.valueOf(customerProductInfo.getInvoicePrice()));
            binding.edTextInvoicePrice.setSelection(binding.edTextInvoicePrice.getText().toString().length());

            if (customerProductInfo.getExpireDate() != 0) {
                String dateFormat = Converter.dateFormat(String.valueOf(customerProductInfo.getExpireDate()));
                binding.btnExpireDate.setText(dateFormat);
            } else {
                binding.btnExpireDate.setText(SipSupportSharedPreferences.getDate(getContext()));
            }

            binding.checkBoxFinish.setChecked(customerProductInfo.isFinish());
            binding.checkBoxInvoicePayment.setChecked(customerProductInfo.isInvoicePayment());
        }

        if (productInfo != null) {
            binding.edTextInvoicePrice.setText(String.valueOf(productInfo.getCost()));
            binding.btnProductName.setText(productInfo.getProductName());
        } else {
            binding.btnExpireDate.setText(SipSupportSharedPreferences.getDate(getContext()));
        }
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

    private void fetchProductInfo(int productID) {
        viewModel.getSipSupporterServiceProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/products/Info/";
        viewModel.fetchProductInfo(path, userLoginKey, productID);
    }

    private void fetchCustomerProductInfo(int customerProductID) {
        viewModel.getSipSupporterServiceCustomerProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Info/";
        viewModel.fetchCustomerProductInfo(path, userLoginKey, customerProductID);
    }

    private void editCustomerProduct(CustomerProductResult.CustomerProductInfo customerProductInfo) {
        viewModel.getSipSupporterServiceProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Edit/";
        viewModel.editCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    private void addCustomerProduct(CustomerProductResult.CustomerProductInfo customerProductInfo) {
        viewModel.getSipSupporterServiceProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Add/";
        viewModel.addCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    private void handleEvents() {
        binding.edTextInvoicePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edTextInvoicePrice.removeTextChangedListener(this);
                try {
                    String text = editable.toString();
                    Long textToLong;
                    if (text.contains(",")) {
                        text = text.replaceAll(",", "");
                    }
                    textToLong = Long.parseLong(text);
                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(textToLong);
                    binding.edTextInvoicePrice.setText(currencyFormat);
                    binding.edTextInvoicePrice.setSelection(binding.edTextInvoicePrice.getText().length());
                } catch (NumberFormatException exception) {
                    Log.e(TAG, exception.getMessage());
                }
                binding.edTextInvoicePrice.addTextChangedListener(this);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnProductName.getText().toString().isEmpty()) {
                    handleError(getString(R.string.select_product_message));
                } else {

                    if (customerProductInfo == null) {
                        customerProductInfo = new CustomerProductResult.CustomerProductInfo();
                        customerProductInfo.setCustomerID(customerID);
                        customerProductInfo.setProductID(productGroupID);
                    }

                    String description = binding.edTextDescription.getText().toString();
                    customerProductInfo.setDescription(description);
                    String price = binding.edTextInvoicePrice.getText().toString().replaceAll(",", "");
                    customerProductInfo.setInvoicePrice(Long.valueOf(price));
                    customerProductInfo.setFinish(binding.checkBoxFinish.isChecked());
                    customerProductInfo.setInvoicePayment(binding.checkBoxInvoicePayment.isChecked());
                    String date = binding.btnExpireDate.getText().toString().replaceAll("/", "");
                    customerProductInfo.setExpireDate(Long.valueOf(date));

                    if (customerProductID == 0) {
                        addCustomerProduct(customerProductInfo);
                    } else {
                        editCustomerProduct(customerProductInfo);
                    }
                }
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = ProductsContainerActivity.start(getContext(), false);
                startActivity(starter);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = binding.btnExpireDate.getText().toString();
                currentYear = Integer.parseInt(currentDate.substring(0, 4));
                currentMonth = Integer.parseInt(currentDate.substring(5, 7));
                currentDay = Integer.parseInt(currentDate.substring(8));

                PersianDatePickerDialog persianDatePickerDialog = new PersianDatePickerDialog(getContext())
                        .setPositiveButtonString("تایید")
                        .setNegativeButton("انصراف")
                        .setMinYear(1300)
                        .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                        .setInitDate(currentYear, currentMonth, currentDay)
                        .setActionTextColor(Color.BLACK)
                        .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                        .setCancelable(false)
                        .setListener(new PersianPickerListener() {
                            @Override
                            public void onDateSelected(PersianPickerDate persianPickerDate) {
                                int year = persianPickerDate.getPersianYear();
                                int month = persianPickerDate.getPersianMonth();
                                int day = persianPickerDate.getPersianDay();
                                if (String.valueOf(month).length() == 1 && String.valueOf(day).length() == 1) {
                                    binding.btnExpireDate.setText(Converter.dateFormat(year + "0" + month + "0" + day));
                                } else if (String.valueOf(month).length() == 1) {
                                    binding.btnExpireDate.setText(Converter.dateFormat(year + "0" + month + "" + day));
                                } else if (String.valueOf(day).length() == 1) {
                                    binding.btnExpireDate.setText(Converter.dateFormat(year + "" + month + "0" + day));
                                } else {
                                    binding.btnExpireDate.setText(Converter.dateFormat(year + "" + month + "" + day));
                                }
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });
                persianDatePickerDialog.show();
            }
        });
    }

    private void setupObserver() {
        viewModel.getAddCustomerProductResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_customer_product_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (customerProductResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerProductResult.getError());
                }
            }
        });

        viewModel.getProductInfoResultSingleLiveEvent().observe(this, new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (productResult.getErrorCode().equals("0")) {
                    productInfo = productResult.getProducts()[0];
                    initViews();
                }
            }
        });

        viewModel.getEditCustomerProductResultSingleLiveEvent().

                observe(this, new Observer<CustomerProductResult>() {
                    @Override
                    public void onChanged(CustomerProductResult customerProductResult) {
                        if (customerProductResult.getErrorCode().equals("0")) {
                            showSuccessDialog(getString(R.string.success_add_edit_customer_product_message));
                            viewModel.getRefresh().setValue(true);
                            dismiss();
                        } else if (customerProductResult.getErrorCode().equals("-9001")) {
                            ejectUser();
                        } else {
                            handleError(customerProductResult.getError());
                        }
                    }
                });

        viewModel.getCustomerProductInfoResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    customerProductInfo = customerProductResult.getCustomerProducts()[0];
                    initViews();
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
    }
}