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
import com.example.sipsupporterapp.eventbus.RefreshEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditCustomerPaymentDialogFragment extends DialogFragment {
    private FragmentAddEditCustomerPaymentDialogBinding binding;
    private CustomerPaymentViewModel viewModel;
    private ServerData serverData;
    private String currentDate, centerName, userLoginKey;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;
    private CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo;
    private CustomerResult.CustomerInfo customerInfo;
    private CaseResult.CaseInfo caseInfo;
    private int customerPaymentID, customerID, caseID, bankAccountID, currentYear, currentMonth, currentDay;

    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_CUSTOMER_ID = "customerID";
    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = AddEditCustomerPaymentDialogFragment.class.getSimpleName();

    public static AddEditCustomerPaymentDialogFragment newInstance(int customerPaymentID, int customerID, int caseID) {
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_PAYMENT_ID, customerPaymentID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putInt(ARGS_CASE_ID, caseID);
        AddEditCustomerPaymentDialogFragment fragment = new AddEditCustomerPaymentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        setupObserver();
        initVariables();
        fetchBankAccounts();

        if (customerPaymentID > 0) {
            fetchCustomerPaymentInfo(customerPaymentID);
        }

        if (customerID > 0) {
            fetchCustomerInfo(customerID);
        }

        if (caseID > 0) {
            fetchCaseInfo(caseID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_customer_payment_dialog,
                null,
                false);

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

    private void initVariables() {
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        caseID = getArguments().getInt(ARGS_CASE_ID);

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        if (customerPaymentInfo != null) {
            binding.txtCustomerName.setText(Converter.letterConverter(customerPaymentInfo.getCustomerName()));

            binding.edTextDescription.setText(Converter.letterConverter(customerPaymentInfo.getDescription()));
            binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());

            binding.edTextCustomerPayment.setText(String.valueOf(customerPaymentInfo.getPrice()));
            binding.edTextCustomerPayment.setSelection(binding.edTextCustomerPayment.getText().length());

            if (customerPaymentInfo.getDatePayment() != 0) {
                String dateFormat = formatDate(customerPaymentInfo.getDatePayment());
                binding.btnDepositDate.setText(dateFormat);
            } else {
                binding.btnDepositDate.setText(SipSupportSharedPreferences.getDate(getContext()));
            }
        } else if (customerInfo != null) {
            binding.txtCustomerName.setText(Converter.letterConverter(customerInfo.getCustomerName()));
            binding.btnDepositDate.setText(SipSupportSharedPreferences.getDate(getContext()));
        } else if (caseInfo != null) {
            binding.txtCustomerName.setText(Converter.letterConverter(caseInfo.getCustomerName()));
            binding.btnDepositDate.setText(SipSupportSharedPreferences.getDate(getContext()));
            customerID = caseInfo.getCustomerID();
        }
    }

    @NotNull
    private String formatDate(int datePayment) {
        String date = String.valueOf(datePayment);
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + "/" + month + "/" + day;
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

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        List<String> bankAccountNameList = new ArrayList<>();
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameList.add(i, Converter.letterConverter(bankAccountInfoArray[i].getBankAccountName()));
        }

        bankAccountID = bankAccountInfoArray[0].getBankAccountID();
        binding.spinnerBankAccountNames.setItems(bankAccountNameList);
    }

    private void fetchCustomerPaymentInfo(int customerPaymentID) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Info/";
        viewModel.fetchCustomerPaymentInfo(path, userLoginKey, customerPaymentID);
    }

    private void fetchCustomerInfo(int customerID) {
        viewModel.getSipSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customers/Info/";
        viewModel.fetchCustomerInfo(path, userLoginKey, customerID);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
    }

    private void fetchBankAccounts() {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void editCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Edit/";
        viewModel.editCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void addCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Add/";
        viewModel.addCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(this, new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult.getErrorCode().equals("0")) {
                    if (bankAccountResult.getBankAccounts().length != 0) {
                        bankAccountInfoArray = bankAccountResult.getBankAccounts();
                        setupSpinner(bankAccountResult.getBankAccounts());
                    }
                } else if (bankAccountResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(bankAccountResult.getError());
                }
            }
        });

        viewModel.getAddCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("ثبت واریزی موفقیت آمیز بود");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    if (caseID != 0) {
                        EventBus.getDefault().post(new RefreshEvent());
                    } else {
                        viewModel.getRefresh().setValue(true);
                    }
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
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("ثبت واریزی موفقیت آمیز بود");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getCustomerPaymentInfoResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    customerPaymentInfo = customerPaymentResult.getCustomerPayments()[0];
                    initViews();
                }
            }
        });

        viewModel.getCustomerInfoResultSingleLiveEvent().observe(this, new Observer<CustomerResult>() {
            @Override
            public void onChanged(CustomerResult customerResult) {
                if (customerResult.getErrorCode().equals("0")) {
                    customerInfo = customerResult.getCustomers()[0];
                    initViews();
                }
            }
        });

        viewModel.getCaseInfoResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    caseInfo = caseResult.getCases()[0];
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

                String description = binding.edTextDescription.getText().toString();
                customerPaymentInfo.setDescription(description);

                String price = binding.edTextCustomerPayment.getText().toString().replaceAll(",", "");
                customerPaymentInfo.setPrice(Long.valueOf(price));

                String datePayment = binding.btnDepositDate.getText().toString().replaceAll("/", "");
                customerPaymentInfo.setDatePayment(Integer.valueOf(datePayment));

                customerPaymentInfo.setCustomerPaymentID(customerPaymentID);
                customerPaymentInfo.setCustomerID(customerID);
                customerPaymentInfo.setCaseID(caseID);
                customerPaymentInfo.setBankAccountID(bankAccountID);

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
                                    binding.btnDepositDate.setText(formatDate(Integer.parseInt(year + "0" + month + "0" + day)));
                                } else if (String.valueOf(month).length() == 1) {
                                    binding.btnDepositDate.setText(formatDate(Integer.parseInt(year + "0" + month + "" + day)));
                                } else if (String.valueOf(day).length() == 1) {
                                    binding.btnDepositDate.setText(formatDate(Integer.parseInt(year + "" + month + "0" + day)));
                                } else {
                                    binding.btnDepositDate.setText(formatDate(Integer.parseInt(year + "" + month + "" + day)));
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

        binding.spinnerBankAccountNames.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
            }
        });
    }
}