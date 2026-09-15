package com.easyservice.backend.infrastructure;

import com.easyservice.backend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FakeNotificationService implements NotificationService {
    private final List<String> notifications = new ArrayList<>();

    @Override
    public void sendNotification(String recipient, String message) {
        notifications.add("Recipient: " + recipient + " | Message: " + message);
    }

    @Override
    public int getNotificationCount() {
        return notifications.size();
    }

    @Override
    public String getLastNotificationMessage() {
        if (notifications.isEmpty()) return null;
        return notifications.get(notifications.size() - 1);
    }

    @Override
    public void clear() {
        notifications.clear();
    }
}
