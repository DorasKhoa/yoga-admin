package com.example.yogaadmin.models;

public class ClassInstance {
    private int id;
    private int courseId;
    private String date;
    private String teacher;
    private String comments;
    private String type;        // Thêm trường mới
    private String dayOfWeek;   // Thêm trường mới

    // Constructor cơ bản (cho tạo mới)
    public ClassInstance(int courseId, String date, String teacher, String comments) {
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }

    // Constructor đầy đủ (không có type và dayOfWeek)
    public ClassInstance(int id, int courseId, String date, String teacher, String comments) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }

    // Constructor mở rộng (có thêm type và dayOfWeek)
    public ClassInstance(int id, int courseId, String date, String teacher,
                         String comments, String type, String dayOfWeek) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters hiện tại
    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getDate() { return date; }
    public String getTeacher() { return teacher; }
    public String getComments() { return comments; }

    // Thêm getters mới
    public String getType() { return type; }
    public String getDayOfWeek() { return dayOfWeek; }

    // Setters hiện tại
    public void setId(int id) { this.id = id; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setDate(String date) { this.date = date; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
    public void setComments(String comments) { this.comments = comments; }

    // Thêm setters mới
    public void setType(String type) { this.type = type; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}