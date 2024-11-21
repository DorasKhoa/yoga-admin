package com.example.yogaadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.R;
import com.example.yogaadmin.models.ClassInstance;

import java.util.ArrayList;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ViewHolder> {
    private ArrayList<ClassInstance> instances;
    private final OnInstanceActionListener listener;

    public interface OnInstanceActionListener {
        void onEditClick(ClassInstance instance);
        void onDeleteClick(ClassInstance instance);
    }

    public ClassInstanceAdapter(ArrayList<ClassInstance> instances, OnInstanceActionListener listener) {
        this.instances = instances;
        this.listener = listener;
    }

    public void updateData(ArrayList<ClassInstance> newInstances) {
        this.instances = newInstances;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_instance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassInstance instance = instances.get(position);
        holder.bind(instance);
    }

    @Override
    public int getItemCount() {
        return instances.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvTeacher;
        private final TextView tvComments;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvComments = itemView.findViewById(R.id.tvComments);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(instances.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(instances.get(position));
                }
            });
        }

        void bind(ClassInstance instance) {
            tvDate.setText(instance.getDate());
            tvTeacher.setText(instance.getTeacher());
            tvComments.setText(instance.getComments());
        }
    }
}