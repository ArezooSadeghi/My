package com.example.sipsupporterapp.adapter;

import android.view.View;
import android.widget.TextView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.model.File;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class FileNodeBinder extends TreeViewBinder<FileNodeBinder.FileNodeHolder> {

    @Override
    public FileNodeHolder provideViewHolder(View itemView) {
        return new FileNodeHolder(itemView);
    }

    @Override
    public void bindView(FileNodeHolder fileNodeHolder, int i, TreeNode treeNode) {
        File fileNode = (File) treeNode.getContent();
        fileNodeHolder.tvFileName.setText(fileNode.getFileName());
    }

    @Override
    public int getLayoutId() {
        return R.layout.file_item;
    }

    public class FileNodeHolder extends TreeViewBinder.ViewHolder {
        private TextView tvFileName;

        public FileNodeHolder(View rootView) {
            super(rootView);
            tvFileName = rootView.findViewById(R.id.tv_dir_name);
        }
    }
}
