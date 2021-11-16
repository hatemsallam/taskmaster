package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.TaskViewHolder>{
    private List<com.amplifyframework.datastore.generated.model.Task> allTasksData = new ArrayList<>();
    private OnTaskItemClickListener listener;

    public TaskAdapter(List<com.amplifyframework.datastore.generated.model.Task> tasks) {
    }


    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
    }

    public TaskAdapter(List<com.amplifyframework.datastore.generated.model.Task> allTasksData, OnTaskItemClickListener listener) {
        this.allTasksData = allTasksData;
        this.listener = listener;

    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        public Task task;
        public TextView title;
        public TextView body;
        public TextView state ;
        View itemView;

        TaskViewHolder(@NonNull View itemView ,OnTaskItemClickListener listener) {
            super(itemView);
            this.itemView =itemView;
            title = itemView.findViewById(R.id.fragment_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(getAdapterPosition());

                }
            });
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task,parent,false);
        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        com.amplifyframework.datastore.generated.model.Task item = allTasksData.get(position);
        taskViewHolder.title.setText(item.getTitle());
//        taskViewHolder.body.setText(item.getBody());
//        taskViewHolder.state.setText(item.getState());
    }


    @Override
    public int getItemCount() {
        return allTasksData.size();
    }
}