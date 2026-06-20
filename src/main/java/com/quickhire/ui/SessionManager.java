package com.quickhire.ui;

import com.quickhire.dao.ApplicationDAO;
import com.quickhire.model.User;

public class SessionManager {

    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }

    public static void setCurrentUser(User user) {
        currentUser = user;
        NotificationStore.clear();
        loadSessionNotifications(user);
    }

    public static void clearSession() {
        currentUser = null;
        NotificationStore.clear();
    }

    public static boolean isLoggedIn() { return currentUser != null; }

    public static void loadSessionNotifications(User user) {
        try {
            ApplicationDAO appDAO = new ApplicationDAO();
            if (user.isSeeker()) {
                // Hired notifications
                var acceptedApps = appDAO.getAcceptedApplicationsForSeeker(user.getUserId());
                for (var app : acceptedApps) {
                    NotificationStore.add("🎉 You were hired for: \""
                            + app.getJobTitle() + "\" by " + app.getProviderName());
                }
                // Active dispute notifications (provider disputed or re-disputed)
                com.quickhire.dao.InvoiceDAO invoiceDAO = new com.quickhire.dao.InvoiceDAO();
                com.quickhire.dao.DisputeDAO disputeDAO = new com.quickhire.dao.DisputeDAO();
                com.quickhire.dao.JobDAO jobDAO = new com.quickhire.dao.JobDAO();
                var inProgressJobs = new com.quickhire.service.JobService()
                        .getJobsBySeeker(user.getUserId());
                for (var job : inProgressJobs) {
                    var invoice = invoiceDAO.getInvoiceByJobId(job.getJobId());
                    if (invoice == null) continue;
                    var dispute = disputeDAO.getDisputeByInvoiceId(invoice.getInvoiceId());
                    if (dispute == null) continue;
                    if (dispute.isAwaitingSeekerResponse()) {
                        NotificationStore.add("⚠️ Dispute on \"" + job.getTitle()
                                + "\" awaits your response. Go to Complete Job → Challenge Dispute.");
                    }
                }
            } else {
                // Pending application notifications
                var pendingApps = appDAO.getPendingApplicationsForProvider(user.getUserId());
                for (var app : pendingApps) {
                    NotificationStore.add("📋 New application for \"" + app.getJobTitle()
                            + "\" from " + app.getSeekerName());
                }
                // Invoice submitted notifications
                com.quickhire.dao.InvoiceDAO invoiceDAO = new com.quickhire.dao.InvoiceDAO();
                com.quickhire.dao.JobDAO jobDAO = new com.quickhire.dao.JobDAO();
                var providerJobs = new com.quickhire.service.JobService()
                        .getJobsByProvider(user.getUserId());
                for (var job : providerJobs) {
                    if (com.quickhire.model.MicroJob.STATUS_PENDING_PAYMENT.equals(job.getStatus())) {
                        var invoice = invoiceDAO.getInvoiceByJobId(job.getJobId());
                        if (invoice != null && invoice.isConfirmedBySeeker()) {
                            NotificationStore.add("🧾 Invoice submitted for \""
                                    + job.getTitle() + "\". Go to Review Invoice.");
                        }
                    }
                    // Challenged dispute notifications
                    if (com.quickhire.model.MicroJob.STATUS_PENDING_PAYMENT.equals(job.getStatus())) {
                        var invoice = invoiceDAO.getInvoiceByJobId(job.getJobId());
                        if (invoice == null) continue;
                        com.quickhire.dao.DisputeDAO disputeDAO = new com.quickhire.dao.DisputeDAO();
                        var dispute = disputeDAO.getDisputeByInvoiceId(invoice.getInvoiceId());
                        if (dispute != null && dispute.isAwaitingProviderResponse()) {
                            NotificationStore.add("💬 Seeker challenged your dispute for \""
                                    + job.getTitle() + "\". Proposed: PKR "
                                    + String.format("%.2f", dispute.getProposedAmount())
                                    + ". Go to Review Invoice.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load session notifications: " + e.getMessage());
        }
    }
}