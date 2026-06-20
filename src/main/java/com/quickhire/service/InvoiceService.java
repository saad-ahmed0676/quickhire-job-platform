package com.quickhire.service;

import com.quickhire.dao.InvoiceDAO;
import com.quickhire.dao.JobDAO;
import com.quickhire.model.Invoice;
import com.quickhire.model.MicroJob;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO       = new InvoiceDAO();
    private final JobDAO jobDAO               = new JobDAO();
    private final NotificationService notifier = new NotificationService();

    // UC07 — Generate invoice
    public Invoice generateInvoice(int jobId, double hoursWorked, double supplyCost)
            throws Exception {
        MicroJob job = jobDAO.getJobById(jobId);
        if (job == null) throw new Exception("Job not found.");
        if (!MicroJob.STATUS_IN_PROGRESS.equals(job.getStatus()))
            throw new Exception("Invoice can only be generated for In Progress jobs.");
        if (hoursWorked <= 0)
            throw new IllegalArgumentException("Hours worked must be greater than zero.");
        if (supplyCost < 0)
            throw new IllegalArgumentException("Supply cost cannot be negative.");

        Invoice invoice = new Invoice();
        invoice.setJobId(jobId);
        invoice.setHoursWorked(hoursWorked);
        invoice.setSupplyCost(supplyCost);
        double total = invoice.calculateTotal(job.getHourlyRate());
        invoice.setTotalAmount(total);
        invoice.setConfirmedBySeeker(false);

        int id = invoiceDAO.insertInvoice(invoice);
        invoice.setInvoiceId(id);
        return invoice;
    }

    // UC07 — Seeker confirms invoice
    public void confirmInvoice(int invoiceId, int jobId) throws Exception {
        invoiceDAO.setConfirmedBySeeker(invoiceId, true);
        jobDAO.updateJobStatus(jobId, MicroJob.STATUS_PENDING_PAYMENT);

        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        MicroJob job    = jobDAO.getJobById(jobId);
        notifier.notifyProvider(job.getProviderId(),
                "Invoice submitted for job: " + job.getTitle() +
                        ". Total: PKR " + invoice.getTotalAmount());
    }

    public Invoice getInvoiceByJobId(int jobId) throws Exception {
        return invoiceDAO.getInvoiceByJobId(jobId);
    }
}