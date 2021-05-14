package com.example.sipsupporterapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.TaskAdapter;
import com.example.sipsupporterapp.databinding.FragmentUserTaskBinding;
import com.example.sipsupporterapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class UserTaskFragment extends Fragment {
    private FragmentUserTaskBinding binding;

    public static UserTaskFragment newInstance() {
        UserTaskFragment fragment = new UserTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_task,
                container,
                false);

        initViews();
        setupAdapter();

        return binding.getRoot();
    }

    private void initViews() {
        binding.recyclerViewUserTasks.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupAdapter() {
        Task task = new Task();
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        TaskAdapter adapter = new TaskAdapter(getContext(), taskList);
        binding.recyclerViewUserTasks.setAdapter(adapter);
    }
}