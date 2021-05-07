package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.view.fragment.FullScreenImageFragment;

public class FullScreenPhotoContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_ATTACH_ID = "attachID";
    private static final String EXTRA_PHOTO = "photo";

    @Override
    public Fragment createFragment() {
        Uri photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
        int attachID = getIntent().getIntExtra(EXTRA_ATTACH_ID, 0);
        return FullScreenImageFragment.newInstance(photo, attachID);
    }

    public static Intent start(Context context, Uri photo, int attachID) {
        Intent starter = new Intent(context, FullScreenPhotoContainerActivity.class);
        starter.putExtra(EXTRA_ATTACH_ID, attachID);
        starter.putExtra(EXTRA_PHOTO, photo);
        return starter;
    }
}