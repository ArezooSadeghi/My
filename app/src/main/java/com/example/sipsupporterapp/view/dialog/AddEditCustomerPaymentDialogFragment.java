package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditCustomerPaymentDialogBinding;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditCustomerPaymentDialogFragment extends DialogFragment {
    private FragmentAddEditCustomerPaymentDialogBinding binding;
    private CustomerPaymentViewModel viewModel;

    private ServerData serverData;
    private String description, lastValueSpinner, currentDate, centerName, userLoginKey;
    private long price;
    private int datePayment, customerID, customerPaymentID, bankAccountID, currentYear, currentMonth, currentDay;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;

    private static final String ARGS_DESCRIPTION = "description";
    private static final String ARGS_PRICE = "price";
    private static final String ARGS_DATE_PAYMENT = "datePayment";
    private static final String ARGS_CUSTOMER_ID = "customerID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_BANK_ACCOUNT_ID = "bankAccountID";

    public static final String TAG = AddEditCustomerPaymentDialogFragment.class.getSimpleName();

    public static AddEditCustomerPaymentDialogFragment newInstance(int customerPaymentID, int customerID, int bankAccountID, int datePayment, long price, String description) {
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_PAYMENT_ID, customerPaymentID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putInt(ARGS_BANK_ACCOUNT_ID, bankAccountID);
        args.putInt(ARGS_DATE_PAYMENT, datePayment);
        args.putLong(ARGS_PRICE, price);
        args.putString(ARGS_DESCRIPTION, description);
        AddEditCustomerPaymentDialogFragment fragment = new AddEditCustomerPaymentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();

        description = getArguments().getString(ARGS_DESCRIPTION);
        price = getArguments().getLong(ARGS_PRICE);
        datePayment = getArguments().getInt(ARGS_DATE_PAYMENT);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        bankAccountID = getArguments().getInt(ARGS_BANK_ACCOUNT_ID);

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());

        if (customerID != 0) {
            fetchBankAccounts();
        }
        setObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_customer_payment_dialog,
                null,
                false);

        if (customerID != 0) {
            binding.spinnerBankAccountNames.setVisibility(View.VISIBLE);
        }

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void fetchBankAccounts() {
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }


    private void editCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        String path = "/api/v1/customerPayments/Edit/";
        viewModel.editCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void addCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        String path = "/api/v1/customerPayments/Add/";
        viewModel.addCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void setObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(this, new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult.getErrorCode().equals("0")) {
                    bankAccountInfoArray = bankAccountResult.getBankAccounts();
                    setupSpinner(bankAccountResult.getBankAccounts());
                } else if (bankAccountResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(bankAccountResult.getError());
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

        viewModel.getAddCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_payment_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().setValue(true);
                    dismiss();
                } else if (customerPaymentResult.getErrorCode().equals(-9001)) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getEditCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_payment_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().setValue(true);
                    dismiss();
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
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

    private void initViews() {
        if (customerID != 0) {
            String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
            binding.txtCustomerName.setText(customerName);
        }

        binding.edTextDescription.setText(description);
        binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());

        if (datePayment != 0) {
            String dateFormat = formatDate();
            binding.btnDepositDate.setText(dateFormat);
        } else {
            binding.btnDepositDate.setText(SipSupportSharedPreferences.getDate(getContext()));
        }
    }

    @NotNull
    private String formatDate() {
        String date = String.valueOf(datePayment);
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + "/" + month + "/" + day;
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo = new CustomerPaymentResult().new CustomerPaymentInfo();

                String bankAccountName = lastValueSpinner;
                customerPaymentInfo.setBankAccountName(bankAccountName);

                customerPaymentInfo.setBankAccountID(bankAccountID);

                String description = binding.edTextDescription.getText().toString();
                customerPaymentInfo.setDescription(description);

                String price = binding.edTextCustomerPayment.getText().toString().replaceAll(",", "");
                customerPaymentInfo.setPrice(Long.valueOf(price));

                String date = binding.btnDepositDate.getText().toString().replaceAll("/", "");
                customerPaymentInfo.setDatePayment(Integer.valueOf(date));

                customerPaymentInfo.setCustomerID(customerID);
                customerPaymentInfo.setCustomerPaymentID(customerPaymentID);

                if (customerPaymentID == 0) {
                    addCustomerPayment(customerPaymentInfo);
                } else {
                    editCustomerPayment(customerPaymentInfo);
                }
            }
        });

        binding.btnDepositDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = binding.btnDepositDate.getText().toString();
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
                                    datePayment = Integer.parseInt(year + "0" + month + "0" + day);
                                    binding.btnDepositDate.setText(formatDate());
                                } else if (String.valueOf(month).length() == 1) {
                                    datePayment = Integer.parseInt(year + "0" + month + "" + day);
                                    binding.btnDepositDate.setText(formatDate());
                                } else if (String.valueOf(day).length() == 1) {
                                    datePayment = Integer.parseInt(year + "" + month + "0" + day);
                                    binding.btnDepositDate.setText(formatDate());
                                } else {
                                    datePayment = Integer.parseInt(year + "" + month + "" + day);
                                    binding.btnDepositDate.setText(formatDate());
                                }
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });
                persianDatePickerDialog.show();
            }
        });

        binding.edTextCustomerPayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edTextCustomerPayment.removeTextChangedListener(this);
                try {
                    String text = editable.toString();
                    Long textToLong;
                    if (text.contains(",")) {
                        text = text.replaceAll(",", "");
                    }
                    textToLong = Long.parseLong(text);
                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(textToLong);
                    binding.edTextCustomerPayment.setText(currencyFormat);
                    binding.edTextCustomerPayment.setSelection(binding.edTextCustomerPayment.getText().length());
                } catch (NumberFormatException exception) {
                    Log.e(TAG, exception.getMessage());
                }
                binding.edTextCustomerPayment.addTextChangedListener(this);
            }
        });

        binding.edTextCustomerPayment.setText(String.valueOf(price));
        binding.edTextCustomerPayment.setSelection(binding.edTextCustomerPayment.getText().length());

        binding.spinnerBankAccountNames.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
            }
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        String[] bankAccountNameArray = new String[bankAccountInfoArray.length];
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameArray[i] = bankAccountInfoArray[i].getBankAccountName();
        }
        if (bankAccountNameArray.length != 0) {
            if (bankAccountID == 0) {
                lastValueSpinner = bankAccountNameArray[0];
                bankAccountID = bankAccountInfoArray[0].getBankAccountID();
                binding.spinnerBankAccountNames.setItems(bankAccountNameArray);
            } else {
                for (int i = 0; i < bankAccountInfoArray.length; i++) {
                    if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                        lastValueSpinner = bankAccountInfoArray[i].getBankAccountName();
                        BankAccountResult.BankAccountInfo bankAccountInfo = bankAccountInfoArray[i];
                        bankAccountID = bankAccountInfo.getBankAccountID();
                        bankAccountNameArray[i] = bankAccountNameArray[0];
                        bankAccountNameArray[0] = lastValueSpinner;
                        bankAccountInfoArray[i] = bankAccountInfoArray[0];
                        bankAccountInfoArray[0] = bankAccountInfo;
                    }
                }
                binding.spinnerBankAccountNames.setItems(bankAccountNameArray);
            }
        }
    }
}