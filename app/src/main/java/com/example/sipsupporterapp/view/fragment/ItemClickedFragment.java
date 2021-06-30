package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentItemClickedBinding;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.CustomerPaymentContainerActivity;
import com.example.sipsupporterapp.view.activity.CustomerProductContainerActivity;
import com.example.sipsupporterapp.view.activity.CustomerSupportContainerActivity;
import com.example.sipsupporterapp.view.activity.UserContainerActivity;
import com.example.sipsupporterapp.view.dialog.ShowInformationCallDialogFragment;

public class ItemClickedFragment extends Fragment {
    private FragmentItemClickedBinding binding;
    private int customerID;

    private static final String ARGS_CUSTOMER_ID = "customerID";

    public static ItemClickedFragment newInstance(int customerID) {
        ItemClickedFragment fragment = new ItemClickedFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_item_clicked,
                container,
                false);

        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        String userName = Converter.letterConverter(SipSupportSharedPreferences.getUserFullName(getContext()));
        binding.txtUserName.setText(userName);
        binding.txtCustomerName.setText(customerName);

        handleEvents();

        return binding.getRoot();
    }

    private void handleEvents() {
        binding.cardViewSupportHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = CustomerSupportContainerActivity.start(getContext(), customerID);
                startActivity(starter);
            }
        });

        binding.cardViewRegisterSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = UserContainerActivity.start(getContext(), customerID);
                startActivity(starter);
            }
        });

        binding.cardViewCustomerProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = CustomerProductContainerActivity.start(getContext(), customerID);
                startActivity(starter);
            }
        });

        binding.cardViewSeeCustomerCallInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInformationCallDialogFragment fragment = ShowInformationCallDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), ShowInformationCallDialogFragment.TAG);
            }
        });

        binding.cardViewCustomerPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starter = CustomerPaymentContainerActivity.start(getContext(), customerID);
                startActivity(starter);
            }
        });
    }
}