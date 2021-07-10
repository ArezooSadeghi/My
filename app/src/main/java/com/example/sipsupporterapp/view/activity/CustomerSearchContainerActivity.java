package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.eventbus.CustomerSearchEvent;
import com.example.sipsupporterapp.view.fragment.CustomerSearchFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerSearchContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return CustomerSearchFragment.newInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getCustomerSearchEvent(CustomerSearchEvent event) {
        finish();
        EventBus.getDefault().removeStickyEvent(true);
    }

    public static Intent start(Context context) {
        return new Intent(context, CustomerSearchContainerActivity.class);
    }
}