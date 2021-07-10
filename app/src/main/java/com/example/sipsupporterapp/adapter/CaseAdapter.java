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
import com.example.sipsupporterapp.databinding.CaseAdapterItemBinding;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.TaskViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;
import java.util.Random;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseHolder> {
    private Context context;
    private TaskViewModel viewModel;
    private List<CaseResult.CaseInfo> caseInfoList;

    final Random random = new Random();

    public CaseAdapter(Context context, TaskViewModel viewModel, List<CaseResult.CaseInfo> caseInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.caseInfoList = caseInfoList;
    }

    @NonNull
    @Override
    public CaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaseHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.case_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CaseHolder holder, int position) {
        holder.bindCaseInfo(caseInfoList.get(position));
        int color = generateRandomColor();
        holder.binding.cardView.setCardBackgroundColor(color);

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("محصول های این case"))
                        .addItem(new PowerMenuItem("امور حسابداری"))
                        .addItem(new PowerMenuItem("صدور فاکتور", R.drawable.ic_file))
                        .addItem(new PowerMenuItem("به همکاران assign", R.drawable.user))
                        .addItem(new PowerMenuItem("ثبت comment", R.drawable.com))
                        .addItem(new PowerMenuItem("اصلاح", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .addItem(new PowerMenuItem("پایان کار"))
                        .addItem(new PowerMenuItem("مشاهده", R.drawable.magnifier))
                        .addItem(new PowerMenuItem("تغییر گروه"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(14)
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getCaseProductsClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getPrintInvoiceClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 3:
                                viewModel.getAssignToOthersClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 4:
                                viewModel.getRegisterCommentClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 5:
                                viewModel.getEditClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 6:
                                viewModel.getDeleteClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 7:
                                viewModel.getCaseFinishClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 9:
                                viewModel.getChangeCaseTypeClicked().setValue(true);
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });
    }

    public int generateRandomColor() {
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + random.nextInt(256)) / 2;
        final int green = (baseGreen + random.nextInt(256)) / 2;
        final int blue = (baseBlue + random.nextInt(256)) / 2;

        return Color.rgb(red, green, blue);
    }

    @Override
    public int getItemCount() {
        return caseInfoList != null ? caseInfoList.size() : 0;
    }

    public class CaseHolder extends RecyclerView.ViewHolder {
        private CaseAdapterItemBinding binding;

        public CaseHolder(CaseAdapterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindCaseInfo(CaseResult.CaseInfo caseInfo) {
            binding.txtCaseID.setText(String.valueOf(caseInfo.getCaseID()));
            binding.txtDescription.setText(caseInfo.getDescription());
            binding.txtCustomerName.setText(caseInfo.getCustomerName());
            binding.txtUserFullName.setText(Converter.letterConverter(caseInfo.getUserFullName()));
            binding.txtAddTime.setText(formatDate(caseInfo.getAddTime()));

           /* if (caseInfo.getCaseProduct().length != 0) {
                binding.recyclerViewProductsNameContainer.setVisibility(View.VISIBLE);

                binding.recyclerViewProductsName.setLayoutManager(new LinearLayoutManager(context));
                binding.recyclerViewProductsName.addItemDecoration(new DividerItemDecoration(
                        binding.recyclerViewProductsName.getContext(),
                        DividerItemDecoration.VERTICAL));

                List<String> productNameList = new ArrayList<>();
                for (CaseProductInfo caseProductInfo : caseInfo.getCaseProduct()) {
                    productNameList.add(caseProductInfo.getProductName());
                }

                ProductAdapter adapter = new ProductAdapter(context, productNameList);
                binding.recyclerViewProductsName.setAdapter(adapter);
            }*/
        }

        private String formatDate(long addTime) {
            String date = String.valueOf(addTime).substring(0, 8);
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);
            return year + "/" + month + "/" + day;
        }
    }
}
