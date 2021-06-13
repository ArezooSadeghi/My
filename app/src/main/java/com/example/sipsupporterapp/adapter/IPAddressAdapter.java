package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.IpAddressAdapterItemBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;

import java.util.List;

public class IPAddressAdapter extends RecyclerView.Adapter<IPAddressAdapter.IPAddressHolder> {

    private Context context;
    private LoginViewModel viewModel;
    private List<ServerData> serverDataList;
    private int lastSelectedPosition = -1;

    public IPAddressAdapter(Context context, LoginViewModel viewModel, List<ServerData> serverDataList) {
        this.context = context;
        this.viewModel = viewModel;
        this.serverDataList = serverDataList;
    }

    @NonNull
    @Override
    public IPAddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IPAddressHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.ip_address_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull IPAddressHolder holder, int position) {
        ServerData serverData = serverDataList.get(position);
        holder.bindServerData(serverData);

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getDeleteIPAddressListSingleLiveEvent().setValue(serverData);
            }
        });

        holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getUpdateIPAddressListSingleLiveEvent().setValue(serverData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serverDataList == null ? 0 : serverDataList.size();
    }

    public class IPAddressHolder extends RecyclerView.ViewHolder {
        private IpAddressAdapterItemBinding binding;

        public IPAddressHolder(IpAddressAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindServerData(ServerData serverData) {
            String centerName = Converter.convert(serverData.getCenterName());
            binding.txtCenterName.setText(centerName);
            binding.txtIpAddress.setText(serverData.getIpAddress());
        }
    }
}
