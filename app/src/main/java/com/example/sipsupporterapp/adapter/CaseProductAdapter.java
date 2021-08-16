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
    private boolean flag;

    public CaseProductAdapter(CaseProductViewModel viewModel, List<CaseProductResult.CaseProductInfo> caseProductInfoList, boolean flag) {
        this.viewModel = viewModel;
        this.caseProductInfoList = caseProductInfoList;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ProductHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.case_product_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        holder.bind(position);
        CaseProductResult.CaseProductInfo caseProductInfo = caseProductInfoList.get(position);
        holder.binding.checkBoxProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    boolean isChecked = holder.binding.checkBoxProductName.isChecked();
                    for (CaseProductResult.CaseProductInfo caseProductInfo : caseProductInfoList) {
                        if (caseProductInfo.getProductID() == caseProductInfo.getProductID()) {
                            caseProductInfo.setSelected(isChecked);
                            viewModel.getUpdate().setValue(caseProductInfo);
                            break;
                        }
                    }
                } else {
                    viewModel.getItemClicked().setValue(caseProductInfo);
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

        public void bind(Integer position) {
            binding.setCaseProductInfo(caseProductInfoList.get(position));
            binding.setCaseProductViewModel(viewModel);
            binding.setPosition(position);
        }
    }
}
