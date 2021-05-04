package com.example.sipsupporterapp.model;

import com.example.sipsupporterapp.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Dir implements LayoutItemType {

    private String dirName;

    public Dir(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dir_item;
    }
}
