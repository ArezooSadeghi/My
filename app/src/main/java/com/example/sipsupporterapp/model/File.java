package com.example.sipsupporterapp.model;

import com.example.sipsupporterapp.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class File implements LayoutItemType {
    private String fileName;

    public File(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.file_item;
    }
}
