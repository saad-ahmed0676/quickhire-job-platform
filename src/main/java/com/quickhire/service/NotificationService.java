package com.quickhire.service;

public class NotificationService {

    public void notifyProvider(int providerId, String message) {
        System.out.println("[NOTIFY → Provider #" + providerId + "] " + message);
        // NotificationStore is checked by SessionManager on login — no action needed here
    }

    public void notifySeeker(int seekerId, String message) {
        System.out.println("[NOTIFY → Seeker #" + seekerId + "] " + message);
        // NotificationStore is checked by SessionManager on login — no action needed here
    }
}