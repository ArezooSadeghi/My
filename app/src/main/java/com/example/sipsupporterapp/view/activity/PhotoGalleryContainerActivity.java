package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.PhotoGalleryFragment;

public class PhotoGalleryContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_CUSTOMER_SUPPORT_ID = "customerSupportID";
    private static final String EXTRA_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String EXTRA_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String EXTRA_PAYMENT_ID = "paymentID";

    @Override
    public Fragment createFragment() {
        int customerSupportID = getIntent().getIntExtra(EXTRA_CUSTOMER_SUPPORT_ID, 0);
        int customerProductID = getIntent().getIntExtra(EXTRA_CUSTOMER_PRODUCT_ID, 0);
        int customerPaymentID = getIntent().getIntExtra(EXTRA_CUSTOMER_PAYMENT_ID, 0);
        int paymentID = getIntent().getIntExtra(EXTRA_PAYMENT_ID, 0);
        return PhotoGalleryFragment.newInstance(customerSupportID, customerProductID, customerPaymentID, paymentID);
    }

    public static Intent start(Context context, int customerSupportID, int customerProductID, int customerPaymentID, int paymentID) {
        Intent starter = new Intent(context, PhotoGalleryContainerActivity.class);
        starter.putExtra(EXTRA_CUSTOMER_SUPPORT_ID, customerSupportID);
        starter.putExtra(EXTRA_CUSTOMER_PRODUCT_ID, customerProductID);
        starter.putExtra(EXTRA_CUSTOMER_PAYMENT_ID, customerPaymentID);
        starter.putExtra(EXTRA_PAYMENT_ID, paymentID);
        return starter;
    }
}