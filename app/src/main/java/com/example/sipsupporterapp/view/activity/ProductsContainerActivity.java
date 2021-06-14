package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.ProductsFragment;

public class ProductsContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return ProductsFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, ProductsContainerActivity.class);
    }
}