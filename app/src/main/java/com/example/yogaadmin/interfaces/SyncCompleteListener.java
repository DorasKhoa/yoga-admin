package com.example.yogaadmin.interfaces;

public interface SyncCompleteListener {
    void onComplete();
    void onError(Exception e);
}