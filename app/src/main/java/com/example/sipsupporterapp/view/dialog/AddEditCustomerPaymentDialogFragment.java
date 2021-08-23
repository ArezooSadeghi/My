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
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;

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

        if (customerPaymentID == 0) {
            fetchBankAccounts();
        }

        if (customerPaymentID > 0) {
            fetchCustomerPaymentInfo(customerPaymentID);
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

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(this, new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult != null) {
                    if (bankAccountResult.getErrorCode().equals("0")) {
                        if (bankAccountResult.getBankAccounts().length != 0) {
                            bankAccountInfoArray = bankAccountResult.getBankAccounts();
                            setupSpinner(bankAccountInfoArray);
                        }
                    } else if (bankAccountResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(bankAccountResult.getError());
                    }
                }
            }
        });

        viewModel.getAddCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult != null) {
                    if (customerPaymentResult.getErrorCode().equals("0")) {
                        showSuccessDialog("واریزی با موفقیت ثبت شد");
                        if (caseID != 0)
                            EventBus.getDefault().post(new RefreshEvent());
                        else
                            viewModel.getRefresh().setValue(true);
                        dismiss();
                    } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(customerPaymentResult.getError());
                    }
                }
            }
        });

        viewModel.getEditCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult != null) {
                    if (customerPaymentResult.getErrorCode().equals("0")) {
                        showSuccessDialog("واریزی با موفقیت ثبت شد");
                        viewModel.getRefresh().setValue(true);
                        dismiss();
                    } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(customerPaymentResult.getError());
                    }
                }
            }
        });

        viewModel.getCustomerPaymentInfoResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                if (customerPaymentResult != null) {
                    if (customerPaymentResult.getErrorCode().equals("0")) {
                        fetchBankAccounts();
                        customerPaymentInfo = customerPaymentResult.getCustomerPayments()[0];
                        initViews();
                    } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(customerPaymentResult.getError());
                    }
                }
            }
        });

        viewModel.getCaseInfoResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult != null) {
                    if (caseResult.getErrorCode().equals("0")) {
                        caseInfo = caseResult.getCases()[0];
                        initViews();
                    } else if (caseResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(caseResult.getError());
                    }
                }
            }
        });
    }

    private void initVariables() {
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        caseID = getArguments().getInt(ARGS_CASE_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void fetchBankAccounts() {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void initViews() {
        if (customerPaymentInfo != null) {
            bankAccountID = customerPaymentInfo.getBankAccountID();
            binding.txtCustomerName.setText(Converter.letterConverter(customerPaymentInfo.getCustomerName()));
            binding.edTextDescription.setText(Converter.letterConverter(customerPaymentInfo.getDescription()));
            binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());
            binding.edTextPrice.setText(String.valueOf(customerPaymentInfo.getPrice()));
            binding.edTextPrice.setSelection(binding.edTextPrice.getText().length());
            if (customerPaymentInfo.getDatePayment() != 0) {
                String dateFormat = Converter.dateFormat(String.valueOf(customerPaymentInfo.getDatePayment()));
                binding.btnDatePayment.setText(dateFormat);
            } else {
                binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
            }
        } else if (caseInfo != null) {
            binding.txtCustomerName.setText(Converter.letterConverter(caseInfo.getCustomerName()));
            binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
        } else {
            binding.txtCustomerName.setText(Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext())));
            binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
        }
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = binding.edTextPrice.getText().toString().replaceAll(",", "");
                if (customerID == 0 && (binding.edTextDescription.getText().toString().isEmpty() || binding.edTextDescription.getText().toString().length() <= 3)) {
                    handleError("توضیحات باید بیش از چهار حرف باشد");
                } else if (price.isEmpty() || Long.valueOf(price) == 0) {
                    handleError("مبلغ نباید خالی یا صفر باشد");
                } else {
                    if (customerPaymentInfo == null) {
                        customerPaymentInfo = new CustomerPaymentResult.CustomerPaymentInfo();
                    }
                    customerPaymentInfo.setDescription(Converter.letterConverter(binding.edTextDescription.getText().toString()));
                    customerPaymentInfo.setPrice(Long.valueOf(price));
                    String datePayment = binding.btnDatePayment.getText().toString().replaceAll("/", "").replaceAll(" ", "");
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
            }
        });

        binding.btnDatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = binding.btnDatePayment.getText().toString().replaceAll(" ", "");
                currentYear = Integer.parseInt(currentDate.substring(0, 4).replaceAll(" ", ""));
                currentMonth = Integer.parseInt(currentDate.substring(5, 7).replaceAll(" ", ""));
                currentDay = Integer.parseInt(currentDate.substring(8).replaceAll(" ", ""));
                PersianDatePickerDialog persianDatePickerDialog = new PersianDatePickerDialog(getContext())
                        .setPositiveButtonString(getString(R.string.ok))
                        .setNegativeButton(getString(R.string.cancel))
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
                                    binding.btnDatePayment.setText(Converter.dateFormat(year + "0" + month + "0" + day));
                                } else if (String.valueOf(month).length() == 1) {
                                    binding.btnDatePayment.setText(Converter.dateFormat(year + "0" + month + "" + day));
                                } else if (String.valueOf(day).length() == 1) {
                                    binding.btnDatePayment.setText(Converter.dateFormat(year + "" + month + "0" + day));
                                } else {
                                    binding.btnDatePayment.setText(Converter.dateFormat(year + "" + month + "" + day));
                                }
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });
                persianDatePickerDialog.show();
            }
        });

        binding.edTextPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edTextPrice.removeTextChangedListener(this);
                try {
                    String text = editable.toString();
                    Long textToLong;
                    if (text.contains(",")) {
                        text = text.replaceAll(",", "");
                    }
                    textToLong = Long.parseLong(text);
                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(textToLong);
                    binding.edTextPrice.setText(currencyFormat);
                    binding.edTextPrice.setSelection(binding.edTextPrice.getText().length());
                } catch (NumberFormatException exception) {
                    Log.e(TAG, exception.getMessage());
                }
                binding.edTextPrice.addTextChangedListener(this);
            }
        });

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
            }
        });
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        List<String> bankAccountNameList = new ArrayList<>();
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameList.add(i, Converter.letterConverter(bankAccountInfoArray[i].getBankAccountName()));
        }
        binding.spinner.setItems(bankAccountNameList);

        if (bankAccountID != 0) {
            for (int i = 0; i < bankAccountInfoArray.length; i++) {
                if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                    binding.spinner.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            bankAccountID = bankAccountInfoArray[0].getBankAccountID();
        }
    }

    private void addCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Add/";
        viewModel.addCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void editCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Edit/";
        viewModel.editCustomerPaymentResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void fetchCustomerPaymentInfo(int customerPaymentID) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Info/";
        viewModel.fetchCustomerPaymentInfo(path, userLoginKey, customerPaymentID);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
    }
}