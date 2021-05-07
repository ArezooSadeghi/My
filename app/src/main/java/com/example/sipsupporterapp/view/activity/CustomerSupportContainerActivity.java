package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.CustomerSupportFragment;

public class CustomerSupportContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_CUSTOMER_ID = "customerID";

    @Override
    public Fragment createFragment() {
        int customerID = getIntent().getIntExtra(EXTRA_CUSTOMER_ID, 0);
        return CustomerSupportFragment.newInstance(customerID);
    }

    public static Intent start(Context context, int customerID) {
        Intent starter = new Intent(context, CustomerSupportContainerActivity.class);
        starter.putExtra(EXTRA_CUSTOMER_ID, customerID);
        return starter;
    }
}