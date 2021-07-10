package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CaseProductAdapterItemBinding;
import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.viewmodel.CaseProductViewModel;

import java.util.List;

public class CaseProductAdapter extends RecyclerView.Adapter<CaseProductAdapter.ProductHolder> {
    private Context context;
    private CaseProductViewModel viewModel;
    private List<CaseProductResult.CaseProductInfo> caseProductInfoList;

    public CaseProductAdapter(Context context, CaseProductViewModel viewModel, List<CaseProductResult.CaseProductInfo> caseProductInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.caseProductInfoList = caseProductInfoList;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.case_product_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        CaseProductResult.CaseProductInfo info = caseProductInfoList.get(position);
        holder.bindCaseProductInfo(info);

        holder.binding.checkBoxProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.binding.checkBoxProductName.isChecked();

                for (CaseProductResult.CaseProductInfo caseProductInfo : caseProductInfoList) {
                    if (caseProductInfo.getProductID() == info.getProductID()) {
                        caseProductInfo.setSelected(isChecked);
                        viewModel.getUpdate().setValue(caseProductInfo);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseProductInfoList != null ? caseProductInfoList.size() : 0;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        private CaseProductAdapterItemBinding binding;

        public ProductHolder(CaseProductAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCaseProductInfo(CaseProductResult.CaseProductInfo caseProductInfo) {
            binding.checkBoxProductName.setText(caseProductInfo.getProductName());
            binding.checkBoxProductName.setChecked(caseProductInfo.isSelected());
        }
    }
}
