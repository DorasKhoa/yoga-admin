package com.example.yogaadmin.activities;

import com.example.yogaadmin.R;
import com.example.yogaadmin.adapters.ClassInstanceAdapter;
import com.example.yogaadmin.database.DatabaseHelper;
import com.example.yogaadmin.models.Course;
import com.example.yogaadmin.models.ClassInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class CourseInstancesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClassInstanceAdapter adapter;
    private DatabaseHelper dbHelper;
    private Course course;
    private TextView tvCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_instances);

        course = (Course) getIntent().getSerializableExtra("course");
        if (course == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        loadInstances();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewInstances);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvCourseName.setText(getString(R.string.course_name_format,
                course.getType(),
                course.getDayOfWeek()));

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        findViewById(R.id.fabAddInstance).setOnClickListener(v ->
                showAddInstanceDialog());
    }

    private void loadInstances() {
        ArrayList<ClassInstance> instances = dbHelper.getClassInstancesForCourse(course.getId());
        adapter.updateData(instances);
    }

    private void showAddInstanceDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class_instance, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Class Instance");
        builder.setView(dialogView);

        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTeacher = dialogView.findViewById(R.id.etTeacher);
        EditText etComments = dialogView.findViewById(R.id.etComments);

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker(etDate, null));

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (validateAndSaveInstance(null, etDate, etTeacher, etComments, dialog)) {
                loadInstances();
            }
        });
    }

    private void showEditInstanceDialog(ClassInstance instance) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class_instance, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Class Instance");
        builder.setView(dialogView);

        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTeacher = dialogView.findViewById(R.id.etTeacher);
        EditText etComments = dialogView.findViewById(R.id.etComments);

        // Fill existing data
        etDate.setText(instance.getDate());
        etTeacher.setText(instance.getTeacher());
        etComments.setText(instance.getComments());

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker(etDate, instance.getDate()));

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (validateAndSaveInstance(instance, etDate, etTeacher, etComments, dialog)) {
                loadInstances();
            }
        });
    }

    private void showDatePicker(EditText etDate, String currentDate) {
        Calendar calendar = Calendar.getInstance();
        if (currentDate != null) {
            String[] dateParts = currentDate.split("/");
            calendar.set(
                    Integer.parseInt(dateParts[2]), // year
                    Integer.parseInt(dateParts[1]) - 1, // month (0-based)
                    Integer.parseInt(dateParts[0]) // day
            );
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    if (isDayMatching(selectedDate, course.getDayOfWeek())) {
                        String dateStr = String.format(Locale.getDefault(),
                                "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        etDate.setText(dateStr);
                    } else {
                        Toast.makeText(this,
                                "Selected date must be a " + course.getDayOfWeek(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean isDayMatching(Calendar date, String dayOfWeek) {
        int dayOfWeekInt = date.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek.toLowerCase()) {
            case "monday": return dayOfWeekInt == Calendar.MONDAY;
            case "tuesday": return dayOfWeekInt == Calendar.TUESDAY;
            case "wednesday": return dayOfWeekInt == Calendar.WEDNESDAY;
            case "thursday": return dayOfWeekInt == Calendar.THURSDAY;
            case "friday": return dayOfWeekInt == Calendar.FRIDAY;
            case "saturday": return dayOfWeekInt == Calendar.SATURDAY;
            case "sunday": return dayOfWeekInt == Calendar.SUNDAY;
            default: return false;
        }
    }

    private boolean validateAndSaveInstance(ClassInstance instance,
                                            EditText etDate,
                                            EditText etTeacher,
                                            EditText etComments,
                                            AlertDialog dialog) {
        String date = etDate.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        if (date.isEmpty() || teacher.isEmpty()) {
            Toast.makeText(this, "Date and Teacher are required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (instance == null) {
            // Add new instance
            long result = dbHelper.addClassInstance(new ClassInstance(
                    course.getId(),
                    date,
                    teacher,
                    comments
            ));
            if (result != -1) {
                dialog.dismiss();
                return true;
            }
        } else {
            // Update existing instance
            instance.setDate(date);
            instance.setTeacher(teacher);
            instance.setComments(comments);
            int result = dbHelper.updateClassInstance(instance);
            if (result > 0) {
                dialog.dismiss();
                return true;
            }
        }

        Toast.makeText(this, "Failed to save class instance",
                Toast.LENGTH_SHORT).show();
        return false;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassInstanceAdapter(new ArrayList<>(), new ClassInstanceAdapter.OnInstanceActionListener() {
            @Override
            public void onEditClick(ClassInstance instance) {
                showEditInstanceDialog(instance);
            }

            @Override
            public void onDeleteClick(ClassInstance instance) {
                new AlertDialog.Builder(CourseInstancesActivity.this)
                        .setTitle("Delete Instance")
                        .setMessage("Are you sure you want to delete this class instance?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dbHelper.deleteClassInstance(instance.getId());
                            loadInstances();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            // Nếu query rỗng, load tất cả instances
            loadInstances();
        } else {
            // Tìm kiếm theo tên giáo viên
            ArrayList<ClassInstance> searchResults =
                    dbHelper.searchClassInstancesByTeacher(course.getId(), query);
            adapter.updateData(searchResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_instances_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_advanced_search) {
            showAdvancedSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAdvancedSearchDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_advanced_search, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupSearchType);
        EditText etSearchDate = dialogView.findViewById(R.id.etSearchDate);
        Spinner spinnerDayOfWeek = dialogView.findViewById(R.id.spinnerDayOfWeek);

        // Setup date picker
        etSearchDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String dateStr = String.format(Locale.getDefault(),
                                "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        etSearchDate.setText(dateStr);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Setup spinner - đổi tên biến adapter thành spinnerAdapter
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(spinnerAdapter);

        // Handle radio button changes
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            etSearchDate.setVisibility(checkedId == R.id.radioDate ? View.VISIBLE : View.GONE);
            spinnerDayOfWeek.setVisibility(checkedId == R.id.radioDayOfWeek ? View.VISIBLE : View.GONE);
        });

        builder.setView(dialogView)
                .setTitle("Advanced Search")
                .setPositiveButton("Search", (dialog, which) -> {
                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioDate) {
                        String date = etSearchDate.getText().toString();
                        if (!date.isEmpty()) {
                            ArrayList<ClassInstance> results = dbHelper.searchInstancesByDate(date);
                            adapter.updateData(results); // Sử dụng adapter của RecyclerView
                        }
                    } else {
                        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
                        ArrayList<ClassInstance> results = dbHelper.searchInstancesByDayOfWeek(dayOfWeek);
                        adapter.updateData(results); // Sử dụng adapter của RecyclerView
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
