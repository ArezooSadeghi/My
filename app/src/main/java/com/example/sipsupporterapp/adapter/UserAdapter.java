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
    private String date;

    public UserAdapter(Context context, UserViewModel viewModel, List<CustomerUserResult.CustomerUserInfo> customerUserInfoList, String date) {
        this.context = context;
        this.viewModel = viewModel;
        this.customerUserInfoList = customerUserInfoList;
        this.date = date;
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
        holder.bindCustomerSupportInfo(customerUserInfoList.get(position));
        String date = customerUserInfoList.get(position).getLastSeen().substring(0, 10);
        if (this.date != null) {
            if (this.date.equals(date)) {
                holder.binding.imgUserStatus.setImageResource(R.drawable.user_online);
               /* holder.binding.txtCustomerUserName.setTextColor(Color.parseColor("#000000"));
                holder.binding.txtLastSeen.setTextColor(Color.parseColor("#000000"));*/
            } else {
                holder.binding.imgUserStatus.setImageResource(R.drawable.offline_user);
               /* holder.binding.txtCustomerUserName.setTextColor(Color.parseColor("#696969"));*/
               /* holder.binding.txtLastSeen.setTextColor(Color.parseColor("#696969"));*/
            }
        }
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getItemClicked().setValue(customerUserInfoList.get(position).getCustomerID());
                SipSupportSharedPreferences.setCustomerUserId(context, customerUserInfoList.get(position).getCustomerUserID());
            }
        });

        String str = (position + 1) + "";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append((char) ((int) str.charAt(i) - 48 + 1632));
        }
        holder.binding.txtRow.setText(stringBuilder.toString());
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
