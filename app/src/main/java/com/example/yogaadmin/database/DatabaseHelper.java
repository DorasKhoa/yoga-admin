package com.example.yogaadmin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yogaadmin.models.Course;
import com.example.yogaadmin.models.ClassInstance;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "YogaDB";

    // Table name
    private static final String TABLE_COURSES = "courses";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_DAY_OF_WEEK = "day_of_week";
    private static final String KEY_TIME = "time";
    private static final String KEY_PRICE = "price";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CAPACITY = "capacity";
    private static final String KEY_DURATION = "duration";

    // Thêm bảng mới cho class instances
    private static final String TABLE_CLASS_INSTANCES = "class_instances";

    // Thêm columns cho bảng class_instances
    private static final String KEY_INSTANCE_ID = "instance_id";
    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEACHER = "teacher";
    private static final String KEY_COMMENTS = "comments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DAY_OF_WEEK + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_CAPACITY + " TEXT,"
                + KEY_DURATION + " TEXT"
                + ")";

        // Bảng mới cho class instances
        String CREATE_INSTANCES_TABLE = "CREATE TABLE " + TABLE_CLASS_INSTANCES + "("
                + KEY_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_COURSE_ID + " INTEGER,"
                + KEY_DATE + " TEXT NOT NULL,"
                + KEY_TEACHER + " TEXT NOT NULL,"
                + KEY_COMMENTS + " TEXT,"
                + "FOREIGN KEY(" + KEY_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + KEY_ID + ")"
                + ")";

        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_INSTANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }

    // Thêm các phương thức CRUD mới:

    // Create - Thêm khóa học mới
    public long addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(KEY_TIME, course.getTime());
        values.put(KEY_PRICE, course.getPrice());
        values.put(KEY_TYPE, course.getType());
        values.put(KEY_DESCRIPTION, course.getDescription());
        values.put(KEY_CAPACITY, course.getCapacity());
        values.put(KEY_DURATION, course.getDuration());

        Log.d("DatabaseHelper", "Adding course: " + course.toString());

        long result = db.insert(TABLE_COURSES, null, values);
        db.close();

        Log.d("DatabaseHelper", "Insert result: " + result);

        return result;
    }

    // Read - Lấy tất cả khóa học
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_COURSES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndexOrThrow(KEY_ID);
                int dayIndex = cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK);
                int timeIndex = cursor.getColumnIndexOrThrow(KEY_TIME);
                int priceIndex = cursor.getColumnIndexOrThrow(KEY_PRICE);
                int typeIndex = cursor.getColumnIndexOrThrow(KEY_TYPE);
                int descIndex = cursor.getColumnIndexOrThrow(KEY_DESCRIPTION);
                int capacityIndex = cursor.getColumnIndexOrThrow(KEY_CAPACITY);
                int durationIndex = cursor.getColumnIndexOrThrow(KEY_DURATION);

                Course course = new Course(
                        cursor.getInt(idIndex),
                        cursor.getString(dayIndex),
                        cursor.getString(timeIndex),
                        cursor.getString(priceIndex),
                        cursor.getString(typeIndex),
                        cursor.getString(descIndex),
                        cursor.getString(capacityIndex),
                        cursor.getString(durationIndex)
                );
                courseList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courseList;
    }

    // Read - Lấy một khóa học theo ID
    public Course getCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, null,
                KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(KEY_ID);
            int dayIndex = cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK);
            int timeIndex = cursor.getColumnIndexOrThrow(KEY_TIME);
            int priceIndex = cursor.getColumnIndexOrThrow(KEY_PRICE);
            int typeIndex = cursor.getColumnIndexOrThrow(KEY_TYPE);
            int descIndex = cursor.getColumnIndexOrThrow(KEY_DESCRIPTION);
            int capacityIndex = cursor.getColumnIndexOrThrow(KEY_CAPACITY);
            int durationIndex = cursor.getColumnIndexOrThrow(KEY_DURATION);

            Course course = new Course(
                    cursor.getInt(idIndex),
                    cursor.getString(dayIndex),
                    cursor.getString(timeIndex),
                    cursor.getString(priceIndex),
                    cursor.getString(typeIndex),
                    cursor.getString(descIndex),
                    cursor.getString(capacityIndex),
                    cursor.getString(durationIndex)
            );
            cursor.close();
            return course;
        }
        return null;
    }

    // Update - Cập nhật khóa học
    public int updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(KEY_TIME, course.getTime());
        values.put(KEY_PRICE, course.getPrice());
        values.put(KEY_TYPE, course.getType());
        values.put(KEY_DESCRIPTION, course.getDescription());
        values.put(KEY_CAPACITY, course.getCapacity());
        values.put(KEY_DURATION, course.getDuration());

        int result = db.update(TABLE_COURSES, values,
                KEY_ID + "=?", new String[]{String.valueOf(course.getId())});
        db.close();
        return result;
    }

    // Delete - Xóa khóa học
    public void deleteCourse(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Xóa tất cả class instances liên quan đến course
        db.delete(TABLE_CLASS_INSTANCES, KEY_COURSE_ID + "=?",
                new String[]{String.valueOf(id)});

        // Xóa course
        db.delete(TABLE_COURSES, KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    // Đếm số lượng khóa học
    public int getCoursesCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // CRUD operations for class instances
    public long addClassInstance(ClassInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_COURSE_ID, instance.getCourseId());
        values.put(KEY_DATE, instance.getDate());
        values.put(KEY_TEACHER, instance.getTeacher());
        values.put(KEY_COMMENTS, instance.getComments());

        return db.insert(TABLE_CLASS_INSTANCES, null, values);
    }

    public ArrayList<ClassInstance> getClassInstancesForCourse(int courseId) {
        ArrayList<ClassInstance> instances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CLASS_INSTANCES +
                " WHERE " + KEY_COURSE_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                ClassInstance instance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INSTANCE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS))
                );
                instances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instances;
    }

    public int updateClassInstance(ClassInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, instance.getDate());
        values.put(KEY_TEACHER, instance.getTeacher());
        values.put(KEY_COMMENTS, instance.getComments());

        return db.update(TABLE_CLASS_INSTANCES, values,
                KEY_INSTANCE_ID + "=?",
                new String[]{String.valueOf(instance.getId())});
    }

    public void deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, KEY_INSTANCE_ID + "=?",
                new String[]{String.valueOf(instanceId)});
    }

    public void cleanupOrphanedInstances() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Xóa tất cả class instances không có course tương ứng
        String deleteQuery =
                "DELETE FROM " + TABLE_CLASS_INSTANCES +
                        " WHERE " + KEY_COURSE_ID + " NOT IN " +
                        "(SELECT " + KEY_ID + " FROM " + TABLE_COURSES + ")";

        db.execSQL(deleteQuery);
        db.close();
    }

    public ArrayList<ClassInstance> searchClassInstancesByTeacher(int courseId, String teacherName) {
        ArrayList<ClassInstance> instances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CLASS_INSTANCES +
                " WHERE " + KEY_COURSE_ID + " = ? AND " +
                KEY_TEACHER + " LIKE ?";

        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{String.valueOf(courseId), "%" + teacherName + "%"});

        if (cursor.moveToFirst()) {
            do {
                ClassInstance instance = new ClassInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INSTANCE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS))
                );
                instances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instances;
    }

    public ArrayList<ClassInstance> searchInstancesByDate(String date) {
        ArrayList<ClassInstance> instances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT ci.*, c.type, c.day_of_week FROM " + TABLE_CLASS_INSTANCES + " ci " +
                "JOIN " + TABLE_COURSES + " c ON ci." + KEY_COURSE_ID + " = c." + KEY_ID +
                " WHERE " + KEY_DATE + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                instances.add(createInstanceFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instances;
    }

    public ArrayList<ClassInstance> searchInstancesByDayOfWeek(String dayOfWeek) {
        ArrayList<ClassInstance> instances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT ci.*, c.type, c.day_of_week FROM " + TABLE_CLASS_INSTANCES + " ci " +
                "JOIN " + TABLE_COURSES + " c ON ci." + KEY_COURSE_ID + " = c." + KEY_ID +
                " WHERE c." + KEY_DAY_OF_WEEK + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{dayOfWeek});

        if (cursor.moveToFirst()) {
            do {
                instances.add(createInstanceFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instances;
    }

    private ClassInstance createInstanceFromCursor(Cursor cursor) {
        int instanceId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INSTANCE_ID));
        int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE));
        String teacher = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TEACHER));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMMENTS));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE));
        String dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAY_OF_WEEK));

        return new ClassInstance(instanceId, courseId, date, teacher, comments, type, dayOfWeek);
    }
} 