package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.FullScreenPhotoFragment;

public class FullScreenPhotoContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_FILE_PATH = "filePath";
    private static final String EXTRA_ATTACH_ID = "attachID";

    @Override
    public Fragment createFragment() {
        String filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        int attachID = getIntent().getIntExtra(EXTRA_ATTACH_ID, 0);
        return FullScreenPhotoFragment.newInstance(filePath, attachID);
    }

    public static Intent start(Context context, String filePath, int attachID) {
        Intent starter = new Intent(context, FullScreenPhotoContainerActivity.class);
        starter.putExtra(EXTRA_FILE_PATH, filePath);
        starter.putExtra(EXTRA_ATTACH_ID, attachID);
        return starter;
    }
}