package com.example.sipsupporterapp.view.dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sipsupporterapp.R;

public class AddEditAssignDialogFragment extends DialogFragment {


    public static AddEditAssignDialogFragment newInstance() {
        AddEditAssignDialogFragment fragment = new AddEditAssignDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}