package com.example.yogaadmin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.example.yogaadmin.R;
import com.example.yogaadmin.adapters.CourseAdapter;
import com.example.yogaadmin.database.DatabaseHelper;
import com.example.yogaadmin.models.Course;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import com.example.yogaadmin.activities.CreateYogaCourseActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.yogaadmin.firebase.FirebaseHelper;
import com.example.yogaadmin.utils.NetworkUtils;
import android.app.ProgressDialog;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvNoData;
    private SwipeRefreshLayout swipeRefresh;
    private FirebaseHelper firebaseHelper;

    private final ActivityResultLauncher<Intent> addCourseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadCourses();
                    Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final CourseAdapter.OnCourseActionListener actionListener = new CourseAdapter.OnCourseActionListener() {
        @Override
        public void onEditClick(Course course, int position) {
            Intent intent = new Intent(MainActivity.this, CreateYogaCourseActivity.class);
            intent.putExtra("course", course);
            intent.putExtra("position", position);
            intent.putExtra("editMode", true);
            addCourseLauncher.launch(intent);
        }

        @Override
        public void onDeleteClick(Course course, int position) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure you want to delete this course?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteCourse(course.getId());
                        loadCourses();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        @Override
        public void onItemClick(Course course) {
            Intent intent = new Intent(MainActivity.this, CourseInstancesActivity.class);
            intent.putExtra("course", course);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        firebaseHelper = new FirebaseHelper(this);
        dbHelper = new DatabaseHelper(this);

        initViews();

        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();

        setupAddButton();

        setupSwipeRefresh();

        loadCourses();

        // Thêm nút sync
        Button btnSync = findViewById(R.id.btnSync);
        btnSync.setOnClickListener(v -> syncToFirebase());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCourses);
        tvNoData = findViewById(R.id.tvNoData);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(new ArrayList<>(), actionListener);
        recyclerView.setAdapter(adapter);
    }

    private void setupAddButton() {
        Button btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateYogaCourseActivity.class);
            addCourseLauncher.launch(intent);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadCourses();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void loadCourses() {
        try {
            ArrayList<Course> courses = dbHelper.getAllCourses();
            Log.d("MainActivity", "Loaded courses: " + courses.size());

            if (courses.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateData(courses);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading courses: " + e.getMessage());
            Toast.makeText(this, "Error loading courses: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void syncToFirebase() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Syncing data to Firebase...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseHelper.syncData(new FirebaseHelper.SyncCompleteListener() {
            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,
                            "Sync completed successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,
                            "Sync failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}