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
import com.example.sipsupporterapp.databinding.CustomerProductAdapeterItemBinding;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductsHolder> {
    private Context context;
    private CustomerProductViewModel viewModel;
    private List<CustomerProductResult.CustomerProductInfo> customerProductInfoList;

    public CustomerProductAdapter(CustomerProductViewModel viewModel, List<CustomerProductResult.CustomerProductInfo> customerProductInfoList) {
        this.viewModel = viewModel;
        this.customerProductInfoList = customerProductInfoList;
    }

    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ProductsHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.customer_product_adapeter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsHolder holder, int position) {
        holder.bind(position);
        CustomerProductResult.CustomerProductInfo customerProductInfo = customerProductInfoList.get(position);
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_edit_item_title), R.drawable.edit))
                        .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_delete_item_title), R.drawable.delete))
                        .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_see_attachment_item_title), R.drawable.see))
                        .setTextColor(Color.BLACK)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextSize(12)
                        .setIconSize(24)
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                viewModel.getEditClicked().setValue(customerProductInfo.getCustomerProductID());
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(customerProductInfo.getCustomerProductID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getSeeCustomerProductAttachmentsClicked().setValue(customerProductInfo);
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
        return customerProductInfoList == null ? 0 : customerProductInfoList.size();
    }

    public class ProductsHolder extends RecyclerView.ViewHolder {
        private CustomerProductAdapeterItemBinding binding;

        public ProductsHolder(CustomerProductAdapeterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Integer position) {
            binding.setCustomerProductInfo(customerProductInfoList.get(position));
            binding.setCustomerProductViewModel(viewModel);
            binding.setPosition(position);
        }
    }
}
