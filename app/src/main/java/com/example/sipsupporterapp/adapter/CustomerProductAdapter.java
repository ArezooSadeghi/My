package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CustomerProductAdapeterItemBinding;
import com.example.sipsupporterapp.model.CustomerProductInfo;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductsHolder> {
    private Context context;
    private List<CustomerProductInfo> customerProductInfoList;
    private CustomerProductViewModel viewModel;

    public CustomerProductAdapter(Context context, List<CustomerProductInfo> customerProductInfoList, CustomerProductViewModel viewModel) {
        this.context = context;
        this.customerProductInfoList = customerProductInfoList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductsHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_product_adapeter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsHolder holder, int position) {
        CustomerProductInfo customerProductInfo = customerProductInfoList.get(position);
        holder.bindCustomerProducts(customerProductInfo);

        int customerProductID = customerProductInfo.getCustomerProductID();

        holder.binding.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .addItem(new PowerMenuItem("مشاهده مستندات"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                viewModel.getEditClicked().setValue(customerProductInfo);
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(customerProductID);
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getProductAdapterSeeDocumentsClickedSingleLiveEvent().setValue(customerProductInfo);
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

        public void bindCustomerProducts(CustomerProductInfo customerProductInfo) {
            String productName = Converter.convert(customerProductInfo.getProductName());
            binding.txtProductName.setText(productName);
            if (!customerProductInfo.getDescription().isEmpty()) {
                binding.txtDescription.setVisibility(View.VISIBLE);
                String description = Converter.convert(customerProductInfo.getDescription());
                binding.txtDescription.setText(description);
            }

            String userFullName = Converter.convert(customerProductInfo.getUserFullName());
            binding.txtUserName.setText(userFullName);

            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(customerProductInfo.getInvoicePrice());

            binding.txtInvoicePrice.setText(formattedNumber + "تومان");

            if (customerProductInfo.isFinish()) {
                binding.checkBoxFinish.setChecked(true);
            } else {
                binding.checkBoxFinish.setChecked(false);
            }

            if (customerProductInfo.isInvoicePayment()) {
                binding.checkBoxInvoicePayment.setChecked(true);
            } else {
                binding.checkBoxInvoicePayment.setChecked(false);
            }
        }
    }
}
