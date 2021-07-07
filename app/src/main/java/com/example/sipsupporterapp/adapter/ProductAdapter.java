package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.ProductAdapterItemBinding;
import com.example.sipsupporterapp.model.CaseProductResult;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    private Context context;
    private List<CaseProductResult.CaseProductInfo> caseProductInfoList;

    public ProductAdapter(Context context, List<CaseProductResult.CaseProductInfo> productInfoList) {
        this.context = context;
        this.caseProductInfoList = productInfoList;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.product_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        holder.bindCaseProductInfo(caseProductInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return caseProductInfoList != null ? caseProductInfoList.size() : 0;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        private ProductAdapterItemBinding binding;

        public ProductHolder(ProductAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCaseProductInfo(CaseProductResult.CaseProductInfo caseProductInfo) {
            binding.checkBoxProductName.setText(caseProductInfo.getProductName());
            binding.checkBoxProductName.setChecked(caseProductInfo.isSelected());
        }
    }
}
