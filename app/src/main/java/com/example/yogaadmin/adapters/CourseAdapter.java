package com.example.yogaadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogaadmin.R;
import com.example.yogaadmin.models.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<Course> courseList;
    private OnCourseActionListener listener;

    // Interface for handling actions
    public interface OnCourseActionListener {
        void onEditClick(Course course, int position);
        void onDeleteClick(Course course, int position);
        void onItemClick(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseActionListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Set all course information
        holder.tvDay.setText("Day: " + course.getDayOfWeek());
        holder.tvTime.setText("Time: " + course.getTime());
        holder.tvType.setText("Type: " + course.getType());
        holder.tvPrice.setText("Price: $" + course.getPrice());
        holder.tvDescription.setText("Description: " + course.getDescription());
        holder.tvCapacity.setText("Capacity: " + course.getCapacity());
        holder.tvDuration.setText("Duration: " + course.getDuration());

        // Set click listeners for buttons
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(course, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(course, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateData(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvType, tvPrice, tvDescription, tvCapacity, tvDuration;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvType = itemView.findViewById(R.id.tvType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Thêm click listener cho toàn bộ item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(courseList.get(position));
                }
            });
        }
    }
}