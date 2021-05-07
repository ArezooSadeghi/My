package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.CustomerFragment;

public class CustomerContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return CustomerFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, CustomerContainerActivity.class);
    }
}