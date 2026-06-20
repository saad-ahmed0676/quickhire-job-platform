package com.quickhire.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationStore {

    public static class Notification {
        private final String message;
        private final LocalDateTime timestamp;
        private boolean read;

        public Notification(String message) {
            this.message   = message;
            this.timestamp = LocalDateTime.now();
            this.read      = false;
        }

        public String getMessage()        { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead()           { return read; }
        public void markRead()            { this.read = true; }

        public String getFormattedTime() {
            return timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        }
    }

    // Separate lists per role — keyed by userId
    private static final List<Notification> notifications = new ArrayList<>();
    private static int unreadCount = 0;

    // Called on login/logout to wipe previous session data
    public static void clear() {
        notifications.clear();
        unreadCount = 0;
    }

    public static void add(String message) {
        notifications.add(0, new Notification(message)); // newest first
        unreadCount++;
    }

    public static List<Notification> getAll() {
        return notifications;
    }

    public static int getUnreadCount() {
        return unreadCount;
    }

    public static void markAllRead() {
        for (Notification n : notifications) n.markRead();
        unreadCount = 0;
    }

    public static boolean hasUnread() {
        return unreadCount > 0;
    }
}