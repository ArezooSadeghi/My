package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.model.Dir;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.DirectoryNodeHolder> {
    private Context context;

    @Override
    public DirectoryNodeHolder provideViewHolder(View itemView) {
        context = itemView.getContext();
        return new DirectoryNodeHolder(itemView);
    }

    @Override
    public void bindView(DirectoryNodeHolder directoryNodeHolder, int i, TreeNode treeNode) {
        directoryNodeHolder.itemView.setPadding(3, 3, treeNode.getHeight() * 30, 3);
        directoryNodeHolder.ivArrow.setRotation(0);
        directoryNodeHolder.ivArrow.setImageResource(R.drawable.ic_arrow_left);

        Drawable folderDrawable = context.getResources().getDrawable(R.drawable.ic_folder);
        folderDrawable = DrawableCompat.wrap(folderDrawable);
        DrawableCompat.setTint(folderDrawable, Color.YELLOW);
        DrawableCompat.setTintMode(folderDrawable, PorterDuff.Mode.SRC_IN);
        directoryNodeHolder.tvDirName.setCompoundDrawablesWithIntrinsicBounds(null, null, folderDrawable, null);

        int rotateDegree = treeNode.isExpand() ? -90 : 0;
        directoryNodeHolder.ivArrow.setRotation(rotateDegree);
        Dir dirNode = (Dir) treeNode.getContent();
        directoryNodeHolder.tvDirName.setText(dirNode.getDirName());
        if (treeNode.isLeaf()) {
            directoryNodeHolder.ivArrow.setVisibility(View.GONE);
            Drawable fileDrawable = context.getResources().getDrawable(R.drawable.ic_file);
            fileDrawable = DrawableCompat.wrap(fileDrawable);
            DrawableCompat.setTint(fileDrawable, Color.WHITE);
            DrawableCompat.setTintMode(fileDrawable, PorterDuff.Mode.SRC_IN);
            directoryNodeHolder.tvDirName.setCompoundDrawablesWithIntrinsicBounds(null, null, fileDrawable, null);
        } else directoryNodeHolder.ivArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dir_item;
    }

    public class DirectoryNodeHolder extends TreeViewBinder.ViewHolder {
        private TextView tvDirName;
        private ImageView ivArrow;

        public DirectoryNodeHolder(View rootView) {
            super(rootView);
            tvDirName = rootView.findViewById(R.id.tv_dir_name);
            ivArrow = rootView.findViewById(R.id.iv_arrow);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }
    }
}
