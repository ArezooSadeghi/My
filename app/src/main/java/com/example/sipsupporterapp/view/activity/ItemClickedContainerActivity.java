package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.ItemClickedFragment;

public class ItemClickedContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_CUSTOMER_ID = "customerID";

    @Override
    public Fragment createFragment() {
        int customerID = getIntent().getIntExtra(EXTRA_CUSTOMER_ID, 0);
        return ItemClickedFragment.newInstance(customerID);
    }

    public static Intent newIntent(Context context, int customerID) {
        Intent intent = new Intent(context, ItemClickedContainerActivity.class);
        intent.putExtra(EXTRA_CUSTOMER_ID, customerID);
        return intent;
    }
}