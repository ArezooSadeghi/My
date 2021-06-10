package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.NewPaymentFragment;

public class NewPaymentContainer extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return NewPaymentFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, NewPaymentContainer.class);
    }
}