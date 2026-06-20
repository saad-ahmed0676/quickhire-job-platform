package com.quickhire.service;

import com.quickhire.dao.JobDAO;
import com.quickhire.model.MicroJob;
import java.util.List;

public class JobService {

    private final JobDAO jobDAO = new JobDAO();

    // UC02 — Post a new job
    public MicroJob postJob(int providerId, String title, String category,
                            String description, String location, double hourlyRate)
            throws Exception {
        // Input validation
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Job title is required.");
        if (category == null || category.trim().isEmpty())
            throw new IllegalArgumentException("Category is required.");
        if (description == null || description.trim().length() < 20)
            throw new IllegalArgumentException("Description must be at least 20 characters.");
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("Location is required.");
        if (hourlyRate <= 0)
            throw new IllegalArgumentException("Hourly rate must be greater than zero.");

        MicroJob job = new MicroJob();
        job.setProviderId(providerId);
        job.setTitle(title.trim());
        job.setCategory(category.trim());
        job.setDescription(description.trim());
        job.setLocation(location.trim());
        job.setHourlyRate(hourlyRate);
        job.setStatus(MicroJob.STATUS_OPEN);

        int id = jobDAO.insertJob(job);
        job.setJobId(id);
        return job;
    }

    // UC03 — Get all open jobs (unfiltered)
    public List<MicroJob> getAllOpenJobs() throws Exception {
        return jobDAO.getJobsByStatus(MicroJob.STATUS_OPEN);
    }

    // UC03 — Filter jobs
    public List<MicroJob> filterJobs(String category, String location, double minRate)
            throws Exception {
        return jobDAO.filterJobs(category, location, minRate);
    }

    public MicroJob getJobById(int jobId) throws Exception {
        return jobDAO.getJobById(jobId);
    }

    // UC12 — Edit existing job (only if OPEN)
    public void editJob(MicroJob updatedJob) throws Exception {
        MicroJob existing = jobDAO.getJobById(updatedJob.getJobId());
        if (existing == null)
            throw new Exception("Job not found.");
        if (!existing.isEditable())
            throw new Exception("Cannot edit a job that is already In Progress or beyond.");

        jobDAO.updateJob(updatedJob);
    }

    // UC12 — Cancel job (only if OPEN)
    public void cancelJob(int jobId) throws Exception {
        MicroJob job = jobDAO.getJobById(jobId);
        if (job == null)
            throw new Exception("Job not found.");
        if (!job.isEditable())
            throw new Exception("Cannot cancel a job that is already In Progress.");

        jobDAO.updateJobStatus(jobId, MicroJob.STATUS_CANCELLED);
    }

    // Used by UC05, UC06, UC07, UC08
    public void updateJobStatus(int jobId, String newStatus) throws Exception {
        jobDAO.updateJobStatus(jobId, newStatus);
    }

    // Get jobs posted by a specific provider (UC10, UC12)
    public List<MicroJob> getJobsByProvider(int providerId) throws Exception {
        return jobDAO.getJobsByProvider(providerId);
    }

    // Get jobs a seeker is involved in (UC10)
    public List<MicroJob> getJobsBySeeker(int seekerId) throws Exception {
        return jobDAO.getJobsBySeeker(seekerId);
    }
}