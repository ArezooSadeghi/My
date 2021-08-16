package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.IpAddressAdapterItemBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class IPAddressAdapter extends RecyclerView.Adapter<IPAddressAdapter.IPAddressHolder> {
    private Context context;
    private LoginViewModel viewModel;
    private List<ServerData> serverDataList;

    public IPAddressAdapter(LoginViewModel viewModel, List<ServerData> serverDataList) {
        this.viewModel = viewModel;
        this.serverDataList = serverDataList;
    }

    @NonNull
    @Override
    public IPAddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new IPAddressHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.ip_address_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IPAddressHolder holder, int position) {
        ServerData serverData = serverDataList.get(position);
        holder.bindServerData(serverData);
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_edit_item_title), R.drawable.edit))
                        .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_delete_item_title), R.drawable.delete))
                        .setTextColor(Color.BLACK)
                        .setTextGravity(Gravity.RIGHT)
                        .setIconSize(24)
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                viewModel.getEditClicked().setValue(serverData);
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(serverData);
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
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
            binding.setServerData(serverData);
        }
    }
}
