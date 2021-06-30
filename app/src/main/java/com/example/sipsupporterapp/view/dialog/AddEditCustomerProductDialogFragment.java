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
import com.example.sipsupporterapp.model.CustomerProductInfo;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ProductInfo;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.ProductsContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditCustomerProductDialogFragment extends DialogFragment {
    public static final String ARGS_PRODUCT_ID = "productID";
    private FragmentAddEditCustomerProductDialogBinding binding;
    private CustomerProductViewModel viewModel;

    private String lastValueSpinner, currentDate, productGroup;
    private int customerID, customerProductID, productID, currentYear, currentMonth, currentDay, productGroupID;
    private boolean finish, invoicePayment;
    private long invoicePrice, expireDate;
    private String description;

    private ProductInfo[] productInfoArray;

    private static final String ARGS_FINISH = "finish";
    private static final String ARGS_CUSTOMER_ID = "customerID";
    private static final String ARGS_DESCRIPTION = "description";
    private static final String ARGS_INVOICE_PRICE = "invoicePrice";
    private static final String ARGS_INVOICE_PAYMENT = "invoicePayment";
    private static final String ARGS_EXPIRE_DATE = "expireDate";
    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";

    public static final String TAG = AddEditCustomerProductDialogFragment.class.getSimpleName();

    public static AddEditCustomerProductDialogFragment newInstance(int customerID,
                                                                   String description,
                                                                   long invoicePrice,
                                                                   boolean invoicePayment,
                                                                   boolean finish, long expireDate, int customerProductID, int productID) {
        AddEditCustomerProductDialogFragment fragment = new AddEditCustomerProductDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putString(ARGS_DESCRIPTION, description);
        args.putLong(ARGS_INVOICE_PRICE, invoicePrice);
        args.putBoolean(ARGS_INVOICE_PAYMENT, invoicePayment);
        args.putBoolean(ARGS_FINISH, finish);
        args.putLong(ARGS_EXPIRE_DATE, expireDate);
        args.putInt(ARGS_CUSTOMER_PRODUCT_ID, customerProductID);
        args.putInt(ARGS_PRODUCT_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        invoicePrice = getArguments().getLong(ARGS_INVOICE_PRICE);
        finish = getArguments().getBoolean(ARGS_FINISH);
        invoicePayment = getArguments().getBoolean(ARGS_INVOICE_PAYMENT);
        description = getArguments().getString(ARGS_DESCRIPTION);
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        expireDate = getArguments().getLong(ARGS_EXPIRE_DATE);
        productID = getArguments().getInt(ARGS_PRODUCT_ID);

        createViewModel();
        fetchProducts();
        setupObserver();
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
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
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

        binding.edTextInvoicePrice.setText(String.valueOf(invoicePrice));
        binding.edTextInvoicePrice.setSelection(binding.edTextInvoicePrice.getText().length());

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnProductName.getText().toString().isEmpty()) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("لطفا نوع محصول را انتخاب نمایید");
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    CustomerProductInfo customerProductInfo = new CustomerProductInfo();

                    String productName = productGroup;
                    customerProductInfo.setProductName(productName);

                    customerProductInfo.setProductID(productGroupID);

                    String description = binding.edTextDescription.getText().toString();
                    customerProductInfo.setDescription(description);

                    String price = binding.edTextInvoicePrice.getText().toString().replaceAll(",", "");
                    customerProductInfo.setInvoicePrice(Long.valueOf(price));

                    boolean paymentPrice;
                    if (binding.checkBoxInvoicePayment.isChecked()) {
                        paymentPrice = true;
                    } else {
                        paymentPrice = false;
                    }

                    boolean finish;
                    if (binding.checkBoxFinish.isChecked()) {
                        finish = true;
                    } else {
                        finish = false;
                    }

                    customerProductInfo.setCustomerID(customerID);
                    customerProductInfo.setCustomerProductID(customerProductID);
                    customerProductInfo.setInvoicePayment(paymentPrice);
                    customerProductInfo.setFinish(finish);

                    String date = binding.btnDateExpiration.getText().toString().replaceAll("/", "");
                    customerProductInfo.setExpireDate(Long.valueOf(date));

                    if (customerProductID == 0) {
                        addProduct(customerProductInfo);
                    } else {
                        editProduct(customerProductInfo);
                    }
                }
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = ProductsContainerActivity.start(getContext());
                startActivity(starter);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnDateExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = binding.btnDateExpiration.getText().toString();
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
                                    expireDate = Long.parseLong(year + "0" + month + "0" + day);
                                    binding.btnDateExpiration.setText(formatDate());
                                } else if (String.valueOf(month).length() == 1) {
                                    expireDate = Integer.parseInt(year + "0" + month + "" + day);
                                    binding.btnDateExpiration.setText(formatDate());
                                } else if (String.valueOf(day).length() == 1) {
                                    expireDate = Integer.parseInt(year + "" + month + "0" + day);
                                    binding.btnDateExpiration.setText(formatDate());
                                } else {
                                    expireDate = Integer.parseInt(year + "" + month + "" + day);
                                    binding.btnDateExpiration.setText(formatDate());
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

    private void fetchProductInfo() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceProductInfo(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/products/Info/";
        viewModel.fetchProductInfo(path, userLoginKey, productGroupID);
    }

    private void editProduct(CustomerProductInfo customerProductInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceEditCustomerProduct(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Edit/";
        viewModel.editCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    private void addProduct(CustomerProductInfo customerProductInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceAddCustomerProduct(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerProducts/Add/";
        viewModel.addCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    private void initViews() {
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);
        binding.edTextDescription.setText(description);
        binding.edTextDescription.setSelection(binding.edTextDescription.getText().toString().length());

        if (expireDate != 0) {
            String dateFormat = formatDate();
            binding.btnDateExpiration.setText(dateFormat);
        } else {
            binding.btnDateExpiration.setText(SipSupportSharedPreferences.getDate(getContext()));
        }

        if (finish) {
            binding.checkBoxFinish.setChecked(true);
        } else {
            binding.checkBoxFinish.setChecked(false);
        }

        if (invoicePayment) {
            binding.checkBoxInvoicePayment.setChecked(true);
        } else {
            binding.checkBoxInvoicePayment.setChecked(false);
        }
    }

    @NotNull
    private String formatDate() {
        String date = String.valueOf(expireDate);
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + "/" + month + "/" + day;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void fetchProducts() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceProductsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/products/List/";
        viewModel.fetchProducts(path, userLoginKey);
    }

    private void setupObserver() {
        viewModel.getProductsResultSingleLiveEvent().observe(this, new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (productResult.getErrorCode().equals("0")) {
                    productInfoArray = productResult.getProducts();
                    setupSpinner(productResult.getProducts());
                    fetchProductInfo();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(productResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getAddCustomerProductResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_product_message));
                    fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getDialogDismissed().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerProductResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getProductInfoResultSingleLiveEvent().observe(this, new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (productResult.getErrorCode().equals("0")) {
                    if (invoicePrice == 0) {
                        binding.edTextInvoicePrice.setText(String.valueOf(productResult.getProducts()[0].getCost()));
                    } else {
                        binding.edTextInvoicePrice.setText(String.valueOf(invoicePrice));
                    }
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(productResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getEditCustomerProductResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                if (customerProductResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_product_message));
                    fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getDialogDismissed().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerProductResult.getError());
                    fragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDangerousUserSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDangerousUser) {
                SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
                SipSupportSharedPreferences.setUserFullName(getContext(), null);
                SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                SipSupportSharedPreferences.setCustomerName(getContext(), null);
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void setupSpinner(ProductInfo[] productInfoArray) {
       /* String[] productNameArray = new String[productInfoArray.length];
        for (int i = 0; i < productNameArray.length; i++) {
            productNameArray[i] = productInfoArray[i].getProductName();
        }
        if (productNameArray.length != 0) {
            if (productID == 0) {
                lastValueSpinner = productNameArray[0];
                productID = productInfoArray[0].getProductID();
                binding.spinnerProducts.setItems(productNameArray);
            } else {
                for (int i = 0; i < productInfoArray.length; i++) {
                    if (productInfoArray[i].getProductID() == productID) {
                        lastValueSpinner = productInfoArray[i].getProductName();
                        ProductInfo productInfo = productInfoArray[i];
                        productID = productInfo.getProductID();
                        productNameArray[i] = productNameArray[0];
                        productNameArray[0] = lastValueSpinner;
                        productInfoArray[i] = productInfoArray[0];
                        productInfoArray[0] = productInfo;
                    }
                }
                binding.spinnerProducts.setItems(productNameArray);
            }
        }*/
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
        productGroup = event.getProductGroup();
        binding.btnProductName.setText(productGroup);
        fetchProductInfo();
        EventBus.getDefault().removeStickyEvent(event);
    }
}