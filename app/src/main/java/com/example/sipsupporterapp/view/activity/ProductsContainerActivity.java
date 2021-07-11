package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.ProductsFragment;

public class ProductsContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_FLAG = "flag";

    @Override
    public Fragment createFragment() {
        boolean flag = getIntent().getBooleanExtra(EXTRA_FLAG, false);
        return ProductsFragment.newInstance(flag);
    }

    public static Intent start(Context context, boolean flag) {
        Intent starter = new Intent(context, ProductsContainerActivity.class);
        starter.putExtra(EXTRA_FLAG, flag);
        return starter;
    }
}