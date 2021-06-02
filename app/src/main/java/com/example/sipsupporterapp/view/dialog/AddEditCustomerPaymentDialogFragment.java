package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditCustomerPaymentDialogBinding;
import com.example.sipsupporterapp.model.BankAccountInfo;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.NumberFormat;
import java.util.Locale;

public class AddEditCustomerPaymentDialogFragment extends DialogFragment {
    private FragmentAddEditCustomerPaymentDialogBinding binding;
    private CustomerPaymentViewModel viewModel;

    private String description, lastValueSpinner;
    private long price;
    private int datePayment, customerID, customerPaymentID, bankAccountID;
    private BankAccountInfo[] bankAccountInfoArray;

    private static final String ARGS_DESCRIPTION = "description";
    private static final String ARGS_PRICE = "price";
    private static final String ARGS_DATE_PAYMENT = "datePayment";
    private static final String ARGS_CUSTOMER_ID = "customerID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_BANK_ACCOUNT_ID = "bankAccountID";

    public static final String TAG = AddEditCustomerPaymentDialogFragment.class.getSimpleName();

    public static AddEditCustomerPaymentDialogFragment newInstance(String description, long price, int datePayment, int customerID, int customerPaymentID, int bankAccountID) {
        AddEditCustomerPaymentDialogFragment fragment = new AddEditCustomerPaymentDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_DESCRIPTION, description);
        args.putLong(ARGS_PRICE, price);
        args.putInt(ARGS_DATE_PAYMENT, datePayment);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putInt(ARGS_CUSTOMER_PAYMENT_ID, customerPaymentID);
        args.putInt(ARGS_BANK_ACCOUNT_ID, bankAccountID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        description = getArguments().getString(ARGS_DESCRIPTION);
        price = getArguments().getLong(ARGS_PRICE);
        datePayment = getArguments().getInt(ARGS_DATE_PAYMENT);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        bankAccountID = getArguments().getInt(ARGS_BANK_ACCOUNT_ID);

        createViewModel();
        fetchBankAccounts();
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

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
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
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceGetBankAccountResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }


    private void editCustomerPayment(CustomerPaymentInfo customerPaymentInfo) {
        ServerData serverData = viewModel.getServerData(SipSupportSharedPreferences.getCenterName(getContext()));
        viewModel.getSipSupportServiceEditCustomerPayments(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Edit/";
        viewModel.editCustomerPaymentsResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void addCustomerPayment(CustomerPaymentInfo customerPaymentInfo) {
        ServerData serverData = viewModel.getServerData(SipSupportSharedPreferences.getCenterName(getContext()));
        viewModel.getSipSupportServiceAddCustomerPayments(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Add/";
        viewModel.addCustomerPaymentsResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), customerPaymentInfo);
    }

    private void setObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(this, new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                bankAccountInfoArray = bankAccountResult.getBankAccounts();
                setupSpinner(bankAccountResult.getBankAccounts());
            }
        });

        viewModel.getErrorBankAccountsResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getNoConnection().observe(this, new Observer<String>() {
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
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                SipSupportSharedPreferences.setCustomerName(getContext(), null);
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewModel.getAddCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_payment_message));
                fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().setValue(true);
                dismiss();
            }
        });

        viewModel.getErrorAddCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getEditCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_customer_payment_message));
                fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                viewModel.getUpdateListAddCustomerPaymentSingleLiveEvent().setValue(true);
                dismiss();
            }
        });

        viewModel.getErrorEditCustomerPaymentResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });
    }

    private void initViews() {
        String customerName = SipSupportSharedPreferences.getCustomerName(getContext());
        if (customerName != null) {
            String customer_Name = Converter.convert(SipSupportSharedPreferences.getCustomerName(getContext()));
            binding.txtCustomerName.setText(customer_Name);
        } else {
            binding.txtCustomerName.setVisibility(View.GONE);
        }

        binding.edTextDescription.setText(description);
        binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());

        if (datePayment != 0) {
            String date = String.valueOf(datePayment);
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);
            String dateFormat = year + "/" + month + "/" + day;
            binding.btnDepositDate.setText(dateFormat);
        } else {
            binding.btnDepositDate.setText(SipSupportSharedPreferences.getDate(getContext()));
        }
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
                CustomerPaymentInfo customerPaymentInfo = new CustomerPaymentInfo();

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
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                if (String.valueOf(monthOfYear + 1).length() == 1 & String.valueOf(dayOfMonth).length() == 1) {
                                    datePayment = Integer.valueOf(year + "0" + (monthOfYear + 1) + "0" + dayOfMonth);
                                    binding.btnDepositDate.setText(year + "/" + "0" + (monthOfYear + 1) + "/" + "0" + dayOfMonth);
                                } else if (String.valueOf(monthOfYear + 1).length() == 1) {
                                    datePayment = Integer.valueOf(year + "0" + (monthOfYear + 1) + dayOfMonth);
                                    binding.btnDepositDate.setText(year + "/" + "0" + (monthOfYear + 1) + "/" + dayOfMonth);
                                } else if (String.valueOf(dayOfMonth).length() == 1) {
                                    datePayment = Integer.valueOf(year + (monthOfYear + 1) + "0" + dayOfMonth);
                                    binding.btnDepositDate.setText(year + "/" + (monthOfYear + 1) + "/" + "0" + dayOfMonth);
                                } else {
                                    datePayment = Integer.valueOf(String.valueOf(year + (monthOfYear + 1) + dayOfMonth));
                                    binding.btnDepositDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                                }
                            }
                        },
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay());
                datePickerDialog.show(getActivity().getFragmentManager(), "datePicker");
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

    private void setupSpinner(BankAccountInfo[] bankAccountInfoArray) {
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
                        BankAccountInfo bankAccountInfo = bankAccountInfoArray[i];
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