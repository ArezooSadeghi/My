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
import com.example.sipsupporterapp.adapter.PaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentPaymentBinding;
import com.example.sipsupporterapp.model.BankAccountInfo;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeletePaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Arrays;
import java.util.List;

public class PaymentFragment extends Fragment {
    private FragmentPaymentBinding binding;
    private PaymentViewModel viewModel;

    private int paymentID, bankAccountID;
    private String lastValueSpinner;
    private BankAccountInfo[] bankAccountInfoArray;

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
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
                R.layout.fragment_payment,
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
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    private void fetchBankAccounts() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceGetBankAccountResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchBankAccounts(userLoginKey);
    }

    private void initViews() {
        String userFullName = Converter.convert(SipSupportSharedPreferences.getUserFullName(getContext()));
        binding.txtUserFullName.setText(userFullName);
        binding.recyclerViewCosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCosts.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCosts.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.fabAddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditPaymentDialogFragment fragment = AddEditPaymentDialogFragment.newInstance(0, "", 0, 0, 0, 0, "");
                fragment.show(getParentFragmentManager(), AddEditPaymentDialogFragment.TAG);
            }
        });

        binding.spinnerBankAccountNames.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
                fetchCostsByBankAccountID();
            }
        });
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                viewModel.getNotifyAddEditCost().setValue(bankAccountResult);
                bankAccountInfoArray = bankAccountResult.getBankAccounts();
                setupSpinner(bankAccountResult.getBankAccounts());
                fetchCostsByBankAccountID();
            }
        });

        viewModel.getErrorBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getPaymentsByBankAccountIDResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                StringBuilder stringBuilder = new StringBuilder();
                String arrayLength = String.valueOf(paymentResult.getPayments().length);

                for (int i = 0; i < arrayLength.length(); i++) {
                    stringBuilder.append((char) ((int) arrayLength.charAt(i) - 48 + 1632));
                }

                binding.txtCountCosts.setText("تعداد برداشت ها: " + stringBuilder.toString());
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.recyclerViewCosts.setVisibility(View.VISIBLE);
                setupAdapter(paymentResult.getPayments());
            }
        });

        viewModel.getErrorPaymentsByBankAccountIDResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
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

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTimeOutExceptionHappen) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.timeout_exception_happen_message));
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
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewModel.getEditClickedSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {
                AddEditPaymentDialogFragment fragment = AddEditPaymentDialogFragment.newInstance(paymentInfo.getPaymentID(), paymentInfo.getDescription(), paymentInfo.getDatePayment(), paymentInfo.getPrice(), paymentInfo.getBankAccountID(), paymentInfo.getPaymentSubjectID(), paymentInfo.getPaymentSubject());
                fragment.show(getParentFragmentManager(), AddEditPaymentDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {
                paymentID = paymentInfo.getPaymentID();
                QuestionDeletePaymentDialogFragment fragment = QuestionDeletePaymentDialogFragment.newInstance(getString(R.string.question_delete_cost_message));
                fragment.show(getParentFragmentManager(), QuestionDeletePaymentDialogFragment.TAG);
            }
        });

        viewModel.getYesDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDelete) {
                deleteCost();
            }
        });

        viewModel.getPaymentDeleteResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_cost_message));
                fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                fetchCostsByBankAccountID();
            }
        });

        viewModel.getErrorPaymentDeleteResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getSeeDocumentsClicked().observe(getViewLifecycleOwner(), new Observer<PaymentInfo>() {
            @Override
            public void onChanged(PaymentInfo paymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, 0, 0, paymentInfo.getPaymentID());
                startActivity(starter);
            }
        });

        viewModel.getUpdateCostListSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer bankAccountID) {
                for (int i = 0; i < bankAccountInfoArray.length; i++) {
                    if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                        PaymentFragment.this.bankAccountID = bankAccountID;
                    }
                }

                setupSpinner(bankAccountInfoArray);
                fetchCostsByBankAccountID();
            }
        });
    }

    private void deleteCost() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServicePaymentsDelete(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.paymentsDelete(userLoginKey, paymentID);
    }

    private void fetchCostsByBankAccountID() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServicePaymentsListByBankAccount(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchPaymentsListByBankAccounts(userLoginKey, bankAccountID);
    }

    private void setupSpinner(BankAccountInfo[] bankAccountInfoArray) {
        String[] bankAccountNameArray = new String[bankAccountInfoArray.length];
        for (int i = 0; i < bankAccountNameArray.length; i++) {
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

    private void setupAdapter(PaymentInfo[] paymentInfoArray) {
        List<PaymentInfo> paymentInfoList = Arrays.asList(paymentInfoArray);
        PaymentAdapter adapter = new PaymentAdapter(getContext(), paymentInfoList, viewModel);
        binding.recyclerViewCosts.setAdapter(adapter);
    }
}