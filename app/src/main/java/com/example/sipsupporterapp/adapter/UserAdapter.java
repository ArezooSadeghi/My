package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.UserAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomerUsersHolder> {

    private Context context;
    private UserViewModel viewModel;
    private List<CustomerUserResult.CustomerUserInfo> customerUserInfoList;
    private String _date;

    public UserAdapter(Context context, UserViewModel viewModel, List<CustomerUserResult.CustomerUserInfo> customerUserInfoList, String _date) {
        this.context = context;
        this.viewModel = viewModel;
        this.customerUserInfoList = customerUserInfoList;
        this._date = _date;
    }

    @NonNull
    @Override
    public CustomerUsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerUsersHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.user_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerUsersHolder holder, int position) {
        CustomerUserResult.CustomerUserInfo customerUserInfo = customerUserInfoList.get(position);
        holder.bindCustomerSupportInfo(customerUserInfo);
        String date = customerUserInfoList.get(position).getLastSeen().substring(0, 10);

        if (_date.equals(date)) {
            holder.binding.imgUserStatus.setImageResource(R.drawable.online_user);
        } else {
            holder.binding.imgUserStatus.setImageResource(R.drawable.offline_user);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getItemClicked().setValue(customerUserInfoList.get(position).getCustomerID());
                SipSupportSharedPreferences.setCustomerUserId(context, customerUserInfoList.get(position).getCustomerUserID());
            }
        });

        String rowNumber = Converter.numberConverter((position + 1) + "");
        holder.binding.txtRow.setText(rowNumber);
    }

    @Override
    public int getItemCount() {
        return customerUserInfoList == null ? 0 : customerUserInfoList.size();
    }

    public class CustomerUsersHolder extends RecyclerView.ViewHolder {
        private UserAdapterItemBinding binding;

        public CustomerUsersHolder(UserAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCustomerSupportInfo(CustomerUserResult.CustomerUserInfo info) {
            String userName = Converter.letterConverter(info.getUserName());
            binding.txtCustomerUserName.setText(userName);
            binding.txtLastSeen.setText(info.getLastSeen());
        }
    }
}
