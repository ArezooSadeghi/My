package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.TaskAdapterItemBinding;
import com.example.sipsupporterapp.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.task_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        holder.bindTask(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public class TaskHolder extends RecyclerView.ViewHolder {
        private TaskAdapterItemBinding binding;

        public TaskHolder(TaskAdapterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindTask(Task task) {

        }
    }
}
