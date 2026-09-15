package com.easyservice.backend.service;

public interface NotificationService {
    void sendNotification(String recipient, String message);
    int getNotificationCount();
    String getLastNotificationMessage();
    void clear();
}
