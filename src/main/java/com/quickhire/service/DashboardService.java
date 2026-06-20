package com.quickhire.service;

import com.quickhire.dao.JobDAO;
import com.quickhire.dao.ProgressUpdateDAO;
import com.quickhire.model.*;
import java.util.List;

public class DashboardService {

    private final JobDAO jobDAO                     = new JobDAO();
    private final ProgressUpdateDAO progressDAO     = new ProgressUpdateDAO();

    // UC10 — Get all jobs for this user (both roles)
    public List<MicroJob> getUserJobs(User user) throws Exception {
        if (user instanceof JobProvider) {
            return jobDAO.getJobsByProvider(user.getUserId());
        } else {
            return jobDAO.getJobsBySeeker(user.getUserId());
        }
    }

    // UC06 — Submit progress update
    public ProgressUpdate submitProgressUpdate(int jobId, String note) throws Exception {
        if (note == null || note.trim().isEmpty())
            throw new IllegalArgumentException("Progress note cannot be empty.");

        ProgressUpdate update = new ProgressUpdate();
        update.setJobId(jobId);
        update.setNote(note.trim());

        int id = progressDAO.insertUpdate(update);
        update.setUpdateId(id);
        return update;
    }

    // UC06 — Get all progress updates for a job
    public List<ProgressUpdate> getProgressUpdates(int jobId) throws Exception {
        return progressDAO.getUpdatesByJobId(jobId);
    }
}