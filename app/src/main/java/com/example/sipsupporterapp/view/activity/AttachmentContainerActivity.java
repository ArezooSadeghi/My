package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.AttachmentFragment;

public class AttachmentContainerActivity extends SingleFragmentActivity {

    private static final String ARGS_CUSTOMER_SUPPORT_ID = "customerSupportID";
    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_PAYMENT_ID = "paymentID";

    @Override
    public Fragment createFragment() {
        int customerSupportID = getIntent().getIntExtra(ARGS_CUSTOMER_SUPPORT_ID, 0);
        int customerProductID = getIntent().getIntExtra(ARGS_CUSTOMER_PRODUCT_ID, 0);
        int customerPaymentID = getIntent().getIntExtra(ARGS_CUSTOMER_PAYMENT_ID, 0);
        int paymentID = getIntent().getIntExtra(ARGS_PAYMENT_ID, 0);
        return AttachmentFragment.newInstance(customerSupportID, customerProductID, customerPaymentID, paymentID);
    }

    public static Intent start(Context context, int customerSupportID, int customerProductID, int customerPaymentID, int paymentID) {
        Intent starter = new Intent(context, AttachmentContainerActivity.class);
        starter.putExtra(ARGS_CUSTOMER_SUPPORT_ID, customerSupportID);
        starter.putExtra(ARGS_CUSTOMER_PRODUCT_ID, customerProductID);
        starter.putExtra(ARGS_CUSTOMER_PAYMENT_ID, customerPaymentID);
        starter.putExtra(ARGS_PAYMENT_ID, paymentID);
        return starter;
    }
}