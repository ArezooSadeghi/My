package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerProducts;
import com.example.sipsupporterapp.model.ProductInfo;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.NumberFormat;
import java.util.Locale;

public class AddEditCustomerProductDialogFragment extends DialogFragment {
    public static final String ARGS_PRODUCT_ID = "productID";
    private FragmentAddEditCustomerProductDialogBinding binding;
    private CustomerProductViewModel viewModel;

    private String lastValueSpinner;
    private int customerID, customerProductID, productID;
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
                CustomerProducts customerProducts = new CustomerProducts();

                String productName = lastValueSpinner;
                customerProducts.setProductName(productName);

                customerProducts.setProductID(productID);

                String description = binding.edTextDescription.getText().toString();
                customerProducts.setDescription(description);

                String price = binding.edTextInvoicePrice.getText().toString().replaceAll(",", "");
                customerProducts.setInvoicePrice(Long.valueOf(price));

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

                customerProducts.setCustomerID(customerID);
                customerProducts.setCustomerProductID(customerProductID);
                customerProducts.setInvoicePayment(paymentPrice);
                customerProducts.setFinish(finish);

                String date = binding.btnDateExpiration.getText().toString().replaceAll("/", "");
                customerProducts.setExpireDate(Long.valueOf(date));

                if (customerProductID == 0) {
                    addProduct(customerProducts);
                } else {
                    editProduct(customerProducts);
                }
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
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                if (String.valueOf(monthOfYear + 1).length() == 1 & String.valueOf(dayOfMonth).length() == 1) {
                                    expireDate = Long.valueOf(year + "0" + (monthOfYear + 1) + "0" + dayOfMonth);
                                    binding.btnDateExpiration.setText(year + "/" + "0" + (monthOfYear + 1) + "/" + "0" + dayOfMonth);
                                } else if (String.valueOf(monthOfYear + 1).length() == 1) {
                                    expireDate = Long.valueOf(year + "0" + (monthOfYear + 1) + dayOfMonth);
                                    binding.btnDateExpiration.setText(year + "/" + "0" + (monthOfYear + 1) + "/" + dayOfMonth);
                                } else if (String.valueOf(dayOfMonth).length() == 1) {
                                    expireDate = Long.valueOf(year + (monthOfYear + 1) + "0" + dayOfMonth);
                                    binding.btnDateExpiration.setText(year + "/" + (monthOfYear + 1) + "/" + "0" + dayOfMonth);
                                } else {
                                    expireDate = Long.valueOf(String.valueOf(year + (monthOfYear + 1) + dayOfMonth));
                                    binding.btnDateExpiration.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                                }
                            }
                        },
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay());
                datePickerDialog.show(getActivity().getFragmentManager(), "datePicker");
            }
        });

        binding.spinnerProducts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                productID = productInfoArray[position].getProductID();

                fetchProductInfo();
            }
        });
    }

    private void fetchProductInfo() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceForGetCustomerProductInfo(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchProductInfo(userLoginKey, productID);
    }

    private void editProduct(CustomerProducts customerProducts) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceForEditCustomerProduct(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.editCustomerProduct(userLoginKey, customerProducts);
    }

    private void addProduct(CustomerProducts customerProducts) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServicePostCustomerProducts(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.postCustomerProducts(userLoginKey, customerProducts);
    }

    private void initViews() {
        String customerName = Converter.convert(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);
        binding.edTextDescription.setText(description);
        binding.edTextDescription.setSelection(binding.edTextDescription.getText().toString().length());

        if (expireDate != 0) {
            String date = String.valueOf(expireDate);
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);
            String dateFormat = year + "/" + month + "/" + day;
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerProductViewModel.class);
    }

    private void fetchProducts() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceGetProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchProductResult(userLoginKey);
    }

    private void setupObserver() {
        viewModel.getProductsResultSingleLiveEvent().observe(this, new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                productInfoArray = productResult.getProducts();
                setupSpinner(productResult.getProducts());
                fetchProductInfo();
            }
        });

        viewModel.getErrorProductsResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getAddProductResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_product_message));
                fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                viewModel.getProductsFragmentDialogDismissSingleLiveEvent().setValue(true);
                dismiss();
            }
        });

        viewModel.getErrorAddProductResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getProductInfoResultSingleLiveEvent().observe(this, new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (invoicePrice == 0) {
                    binding.edTextInvoicePrice.setText(String.valueOf(productResult.getProducts()[0].getCost()));
                } else {
                    binding.edTextInvoicePrice.setText(String.valueOf(invoicePrice));
                }
            }
        });

        viewModel.getErrorProductInfoResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getEditProductResultSingleLiveEvent().observe(this, new Observer<CustomerProductResult>() {
            @Override
            public void onChanged(CustomerProductResult customerProductResult) {
                SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_product_message));
                fragment.show(getActivity().getSupportFragmentManager(), SuccessDialogFragment.TAG);
                viewModel.getProductsFragmentDialogDismissSingleLiveEvent().setValue(true);
                dismiss();
            }
        });

        viewModel.getErrorEditProductResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getNoConnection().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTimeOutExceptionHappen) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.timeout_exception_happen_message));
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
        String[] productNameArray = new String[productInfoArray.length];
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
        }
    }
}