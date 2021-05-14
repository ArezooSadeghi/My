package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.UserTaskFragment;

public class UserTaskContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return UserTaskFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, UserTaskContainerActivity.class);
    }
}