package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.IPAddressAdapter;
import com.example.sipsupporterapp.databinding.FragmentIPAddressListDialogBinding;
import com.example.sipsupporterapp.eventbus.DeleteIPAddressEvent;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class IPAddressListDialogFragment extends DialogFragment {
    private FragmentIPAddressListDialogBinding binding;
    private LoginViewModel viewModel;

    public static final String TAG = IPAddressListDialogFragment.class.getSimpleName();


    public static IPAddressListDialogFragment newInstance() {
        IPAddressListDialogFragment fragment = new IPAddressListDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        setObserver();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_i_p_address_list_dialog,
                null,
                false);


        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        initRecyclerView();

        if (viewModel.getServerDataList().size() != 0) {
            binding.txtNoAddress.setVisibility(View.GONE);
            binding.recyclerViewIpAddress.setVisibility(View.VISIBLE);
            setupAdapter();
        }

        setListener();

        return dialog;
    }

    private void initRecyclerView() {
        binding.recyclerViewIpAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider));
        binding.recyclerViewIpAddress.addItemDecoration(dividerItemDecoration);
    }

    private void setupAdapter() {
        List<ServerData> serverDataList = viewModel.getServerDataList();
        if (serverDataList.size() == 0) {
            binding.txtNoAddress.setVisibility(View.VISIBLE);
            binding.recyclerViewIpAddress.setVisibility(View.GONE);
        } else {
            IPAddressAdapter adapter = new IPAddressAdapter(getContext(), viewModel, serverDataList);
            binding.recyclerViewIpAddress.setAdapter(adapter);
        }
    }


    private void setListener() {
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditIPAddressDialogFragment fragment = AddEditIPAddressDialogFragment.newInstance("", "", "");
                fragment.show(getParentFragmentManager(), AddEditIPAddressDialogFragment.TAG);
                dismiss();
            }
        });
    }


    private void setObserver() {
        viewModel.getInsertIPAddressListSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isInsert) {
                setupAdapter();
            }
        });

        viewModel.getDeleteClicked().observe(this, new Observer<ServerData>() {
            @Override
            public void onChanged(ServerData serverData) {
                QuestionDeleteServerDataDialogFragment fragment = QuestionDeleteServerDataDialogFragment.newInstance("آیا می خواهید آدرس مربوطه را حذف کنید؟", serverData);
                fragment.show(getParentFragmentManager(), QuestionDeleteServerDataDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteIPAddressList().observe(this, new Observer<ServerData>() {
            @Override
            public void onChanged(ServerData serverData) {
                viewModel.deleteServerData(serverData);
                setupAdapter();
                EventBus.getDefault().post(new DeleteIPAddressEvent(serverData));
            }
        });

        viewModel.getEditClicked().observe(this, new Observer<ServerData>() {
            @Override
            public void onChanged(ServerData serverData) {
                String centerName = serverData.getCenterName();
                String ipAddress = serverData.getIpAddress();
                String port = serverData.getPort();

                AddEditIPAddressDialogFragment fragment = AddEditIPAddressDialogFragment.newInstance(centerName, ipAddress, port);
                fragment.show(getParentFragmentManager(), AddEditIPAddressDialogFragment.TAG);

                viewModel.deleteServerData(serverData);
            }
        });
    }
}