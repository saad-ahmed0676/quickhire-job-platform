package com.quickhire.service;

import com.quickhire.dao.ApplicationDAO;
import com.quickhire.dao.JobDAO;
import com.quickhire.model.JobApplication;
import com.quickhire.model.MicroJob;
import java.util.List;

public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final JobDAO jobDAO                 = new JobDAO();
    private final NotificationService notifier  = new NotificationService();

    // UC04 — Submit an application
    public JobApplication submitApplication(int jobId, int seekerId, String note)
            throws Exception {
        // Guard: job must be OPEN
        MicroJob job = jobDAO.getJobById(jobId);
        if (job == null)
            throw new Exception("Job does not exist.");
        if (!job.isOpen())
            throw new Exception("This job is no longer accepting applications.");

        // Guard: seeker hasn't already applied
        if (applicationDAO.hasApplied(jobId, seekerId))
            throw new Exception("You have already applied for this job.");

        JobApplication app = new JobApplication();
        app.setJobId(jobId);
        app.setSeekerId(seekerId);
        app.setApplicationNote(note);
        app.setStatus(JobApplication.STATUS_PENDING);

        int id = applicationDAO.insertApplication(app);
        app.setApplicationId(id);

        // Notify provider
        notifier.notifyProvider(job.getProviderId(),
                "New application received for: " + job.getTitle());
        return app;
    }

    // UC05 — Get all applications for a job
    public List<JobApplication> getApplicationsForJob(int jobId) throws Exception {
        return applicationDAO.getApplicationsForJob(jobId);
    }

    // UC05 — Hire an applicant
    public void hireApplicant(int applicationId, int jobId) throws Exception {
        // Accept the chosen application
        applicationDAO.updateApplicationStatus(applicationId, JobApplication.STATUS_ACCEPTED);

        // Reject all others for the same job
        applicationDAO.rejectOtherApplications(jobId, applicationId);

        // Update job status
        jobDAO.updateJobStatus(jobId, MicroJob.STATUS_IN_PROGRESS);

        // Get hired seeker ID to notify them
        JobApplication hired = applicationDAO.getApplicationById(applicationId);
        notifier.notifySeeker(hired.getSeekerId(),
                "Congratulations! You have been hired.");
    }
}