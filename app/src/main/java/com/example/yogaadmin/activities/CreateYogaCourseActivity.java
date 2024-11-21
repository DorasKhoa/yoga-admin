package com.example.yogaadmin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;

import com.example.yogaadmin.R;
import com.example.yogaadmin.database.DatabaseHelper;
import com.example.yogaadmin.models.Course;

import java.util.Locale;

public class CreateYogaCourseActivity extends AppCompatActivity {
    private Spinner spinnerDayOfWeek, spinnerCourseType;
    private EditText etTime, etPrice, etDescription, etCapacity, etDuration;
    private DatabaseHelper dbHelper;
    private Course existingCourse;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yoga_course);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupSpinners();
        setupTimePicker();

        Intent intent = getIntent();
        if (intent.hasExtra("course")) {
            isEditMode = true;
            existingCourse = (Course) intent.getSerializableExtra("course");
            fillExistingData();
        }

        setupSaveButton();
    }

    private void initViews() {
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        spinnerCourseType = findViewById(R.id.spinnerCourseType);
        etTime = findViewById(R.id.etTime);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(daysAdapter);

        ArrayAdapter<CharSequence> typesAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_types, android.R.layout.simple_spinner_item);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseType.setAdapter(typesAdapter);
    }

    private void setupTimePicker() {
        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String formattedTime = String.format(Locale.getDefault(),
                            "%02d:%02d", hourOfDay, minute);
                    etTime.setText(formattedTime);
                },
                12,
                0,
                false
        );
        timePickerDialog.show();
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveCourse());
    }

    private void fillExistingData() {
        int dayPosition = getDayPosition(existingCourse.getDayOfWeek());
        int typePosition = getTypePosition(existingCourse.getType());

        spinnerDayOfWeek.setSelection(dayPosition);
        spinnerCourseType.setSelection(typePosition);

        etTime.setText(existingCourse.getTime());
        etPrice.setText(existingCourse.getPrice());
        etDescription.setText(existingCourse.getDescription());
        etCapacity.setText(existingCourse.getCapacity());
        etDuration.setText(existingCourse.getDuration());
    }

    private void saveCourse() {
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
        String type = spinnerCourseType.getSelectedItem().toString();
        String time = etTime.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String capacity = etCapacity.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();

        if (time.isEmpty() || price.isEmpty() ||
                description.isEmpty() || capacity.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long result;
            if (isEditMode && existingCourse != null) {
                Course updatedCourse = new Course(
                        existingCourse.getId(),
                        dayOfWeek,
                        time,
                        price,
                        type,
                        description,
                        capacity,
                        duration
                );
                result = dbHelper.updateCourse(updatedCourse);
            } else {
                Course newCourse = new Course(
                        dayOfWeek,
                        time,
                        price,
                        type,
                        description,
                        capacity,
                        duration
                );
                result = dbHelper.addCourse(newCourse);
            }

            if (result != -1) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to save course", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("CreateYogaCourse", "Error saving course: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getDayPosition(String day) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerDayOfWeek.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(day)) {
                return i;
            }
        }
        return 0;
    }

    private int getTypePosition(String type) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerCourseType.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(type)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
