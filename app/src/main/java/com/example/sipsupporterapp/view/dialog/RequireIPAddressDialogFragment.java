package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sipsupporterapp.R;

public class RequireIPAddressDialogFragment extends DialogFragment {

    public static final String TAG = RequireIPAddressDialogFragment.class.getSimpleName();


    public static RequireIPAddressDialogFragment newInstance() {
        RequireIPAddressDialogFragment fragment = new RequireIPAddressDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setMessage("لطفا آدرس ip خود را وارد کنید")
                .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddEditIPAddressDialogFragment fragment = AddEditIPAddressDialogFragment.newInstance("", "", "");
                        fragment.setCancelable(false);
                        fragment.show(getActivity().getSupportFragmentManager(), AddEditIPAddressDialogFragment.TAG);
                        dismiss();
                    }
                })
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }
}