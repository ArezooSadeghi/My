package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.AttachmentAdapterItemBinding;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.PhotoGalleryHolder> {
    private Context context;
    private AttachmentViewModel viewModel;
    private List<String> filePathList;

    public PhotoGalleryAdapter(AttachmentViewModel viewModel, List<String> filePathList) {
        this.viewModel = viewModel;
        this.filePathList = filePathList;
    }

    @NonNull
    @Override
    public PhotoGalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PhotoGalleryHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.attachment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoGalleryHolder holder, int position) {
        String filePath = filePathList.get(position);
        holder.bindFilePath(filePath);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getPhotoClicked().setValue(filePath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filePathList != null ? filePathList.size() : 0;
    }

    public void updateFilePathList(List<String> newFilePathList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PhotoGalleryDiffUtil(filePathList, newFilePathList));
        diffResult.dispatchUpdatesTo(this);
        filePathList.clear();
        filePathList.addAll(newFilePathList);
    }

    public class PhotoGalleryHolder extends RecyclerView.ViewHolder {
        private AttachmentAdapterItemBinding binding;

        public PhotoGalleryHolder(AttachmentAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindFilePath(String filePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                Glide.with(context).load(bitmap).into(binding.imgView);
            }
        }
    }
}
