package com.example.yogaadmin.firebase;

import android.content.Context;
import android.util.Log;

import com.example.yogaadmin.database.DatabaseHelper;
import com.example.yogaadmin.models.Course;
import com.example.yogaadmin.models.ClassInstance;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private final FirebaseFirestore db;
    private final DatabaseHelper dbHelper;
    private final Context context;
    private boolean isSyncing = false;

    public interface SyncCompleteListener {
        void onComplete();
        void onError(Exception e);
    }

    public interface OnUploadListener {
        void onSuccess();
        void onError(Exception e);
    }

    public FirebaseHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.dbHelper = new DatabaseHelper(context);
    }

    public void syncData(SyncCompleteListener listener) {
        if (isSyncing) {
            return;
        }

        isSyncing = true;

        try {
            List<Course> courses = dbHelper.getAllCourses();
            AtomicInteger completedOperations = new AtomicInteger(0);
            AtomicInteger totalOperations = new AtomicInteger(courses.size());

            Log.d(TAG, "Total operations to perform: " + totalOperations.get());

            if (totalOperations.get() == 0) {
                Log.d(TAG, "No data to sync");
                isSyncing = false;
                listener.onComplete();
                return;
            }

            for (Course course : courses) {
                uploadCourse(course, new OnUploadListener() {
                    @Override
                    public void onSuccess() {
                        List<ClassInstance> instances = dbHelper.getClassInstancesForCourse(course.getId());

                        if (instances.isEmpty()) {
                            if (completedOperations.incrementAndGet() == totalOperations.get()) {
                                isSyncing = false;
                                listener.onComplete();
                            }
                        } else {
                            uploadInstances(course.getId(), instances, new OnUploadListener() {
                                @Override
                                public void onSuccess() {
                                    if (completedOperations.incrementAndGet() == totalOperations.get()) {
                                        isSyncing = false;
                                        listener.onComplete();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    isSyncing = false;
                                    listener.onError(e);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        isSyncing = false;
                        listener.onError(e);
                    }
                });
            }
        } catch (Exception e) {
            isSyncing = false;
            listener.onError(e);
        }
    }

    private void uploadCourse(Course course, OnUploadListener listener) {
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("dayOfWeek", course.getDayOfWeek());
        courseData.put("time", course.getTime());
        courseData.put("type", course.getType());
        courseData.put("price", course.getPrice());
        courseData.put("description", course.getDescription());
        courseData.put("capacity", course.getCapacity());
        courseData.put("duration", course.getDuration());

        db.collection("courses")
                .document(String.valueOf(course.getId()))
                .set(courseData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e));
    }

    private void uploadInstances(int courseId, List<ClassInstance> instances, OnUploadListener listener) {
        AtomicInteger completed = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(instances.size());

        for (ClassInstance instance : instances) {
            Map<String, Object> instanceData = new HashMap<>();
            instanceData.put("courseId", instance.getCourseId());
            instanceData.put("date", instance.getDate());
            instanceData.put("teacher", instance.getTeacher());
            instanceData.put("comments", instance.getComments());

            db.collection("courses")
                    .document(String.valueOf(courseId))
                    .collection("instances")
                    .document(String.valueOf(instance.getId()))
                    .set(instanceData)
                    .addOnSuccessListener(aVoid -> {
                        if (completed.incrementAndGet() == total.get()) {
                            listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(e -> listener.onError(e));
        }
    }
}