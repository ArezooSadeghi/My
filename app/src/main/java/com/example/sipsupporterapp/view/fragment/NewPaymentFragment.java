package com.example.sipsupporterapp.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.PaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentNewPaymentBinding;
import com.example.sipsupporterapp.model.BankAccountInfo;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeleteNewPaymentFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeletePaymentDialogFragment;
import com.example.sipsupporterapp.viewmodel.NewPaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class NewPaymentFragment extends Fragment {
    private FragmentNewPaymentBinding binding;
    private NewPaymentViewModel viewModel;

    private String lastValueSpinner;
    private int paymentID;

    private List<Integer> bankAccountIDs = new ArrayList<>();
    List<String> bankAccountNameList = new ArrayList<>();

    public static NewPaymentFragment newInstance() {
        NewPaymentFragment fragment = new NewPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        fetchBankAccounts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_payment,
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
        viewModel = new ViewModelProvider(this).get(NewPaymentViewModel.class);
    }

    private void initViews() {
        binding.recyclerViewPayments.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchBankAccounts() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceGetBankAccountResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                setupSpinner(bankAccountResult.getBankAccounts());
            }
        });

        viewModel.getErrorBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.recyclerViewPayments.setVisibility(View.VISIBLE);
                setupAdapter(paymentResult.getPayments());
            }
        });

        viewModel.getErrorPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getEditClickedSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {

            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {
                paymentID = paymentInfo.getPaymentID();
                QuestionDeleteNewPaymentFragment fragment = QuestionDeleteNewPaymentFragment.newInstance(getString(R.string.question_delete_cost_message));
                fragment.show(getParentFragmentManager(), QuestionDeletePaymentDialogFragment.TAG);
            }
        });

        viewModel.getDeletePaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                Log.d("Arezoo", "Hi");
            }
        });

        viewModel.getErrorDeletePaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getSeeDocumentsClicked().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {
            }
        });

        viewModel.getYesDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("Arezoo", "Called");
                deletePayment();
            }
        });
    }

    private void deletePayment() {
        Log.d("Arezoo", paymentID + "");
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServicePaymentsDelete(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Delete/";
        viewModel.paymentsDelete(path, userLoginKey, paymentID);
    }

    private void setupSpinner(BankAccountInfo[] bankAccountInfoArray) {
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameList.add(i, bankAccountInfoArray[i].getBankAccountName());
            bankAccountIDs.add(i, bankAccountInfoArray[i].getBankAccountID());
        }
        binding.spinnerBankAccounts.setItems(bankAccountNameList);
        lastValueSpinner = bankAccountNameList.get(0);
        fetchPaymentsByBankAccount(bankAccountIDs.get(0));
    }

    private void handleEvents() {
        binding.spinnerBankAccounts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                for (int i = 0; i < bankAccountNameList.size(); i++) {
                    if (lastValueSpinner.equals(bankAccountNameList.get(i))) {
                        fetchPaymentsByBankAccount(bankAccountIDs.get(i));
                        break;
                    }
                }
            }
        });

        binding.fabAddNewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bankAccountID = 0;
                for (int i = 0; i < bankAccountNameList.size(); i++) {
                    if (lastValueSpinner.equals(bankAccountNameList.get(i))) {
                        bankAccountID = bankAccountIDs.get(i);
                        break;
                    }
                }
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance("", 0, 0, 0, 0, bankAccountID);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });
    }

    private void fetchPaymentsByBankAccount(int bankAccountID) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServicePaymentsByBankAccount(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/ListByBankAccount/";
        viewModel.fetchPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    private void setupAdapter(PaymentInfo[] paymentInfoArray) {
        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        for (PaymentInfo info : paymentInfoArray) {
            paymentInfoList.add(info);
        }
        PaymentAdapter adapter = new PaymentAdapter(getContext(), paymentInfoList, viewModel);
        binding.recyclerViewPayments.setAdapter(adapter);
    }
}