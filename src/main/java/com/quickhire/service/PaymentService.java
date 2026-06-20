package com.quickhire.service;

import com.quickhire.dao.InvoiceDAO;
import com.quickhire.dao.PaymentDAO;
import com.quickhire.dao.DisputeDAO;
import com.quickhire.dao.ApplicationDAO;
import com.quickhire.dao.JobDAO;
import com.quickhire.model.*;

public class PaymentService {

    private final PaymentDAO  paymentDAO  = new PaymentDAO();
    private final InvoiceDAO  invoiceDAO  = new InvoiceDAO();
    private final DisputeDAO  disputeDAO  = new DisputeDAO();
    private final JobDAO      jobDAO      = new JobDAO();
    private final NotificationService notifier = new NotificationService();

    // UC08 — Provider approves and pays full invoice amount
    public Payment settlePayment(int invoiceId) throws Exception {
        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) throw new Exception("Invoice not found.");
        if (!invoice.isConfirmedBySeeker())
            throw new Exception("Invoice has not yet been confirmed by the seeker.");

        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentMethod("SIMULATED");

        int id = paymentDAO.insertPayment(payment);
        payment.setPaymentId(id);

        // Resolve any open dispute
        Dispute dispute = disputeDAO.getDisputeByInvoiceId(invoiceId);
        if (dispute != null && !Dispute.STATUS_RESOLVED.equals(dispute.getResolutionStatus())) {
            disputeDAO.resolveDispute(dispute.getDisputeId());
        }

        jobDAO.updateJobStatus(invoice.getJobId(), MicroJob.STATUS_SETTLED);

        // Notify seeker
        MicroJob job = jobDAO.getJobById(invoice.getJobId());
        int seekerId = getHiredSeekerId(job.getJobId());
        notifier.notifySeeker(seekerId,
                "✅ Payment of PKR " + String.format("%.2f", invoice.getTotalAmount())
                        + " received for: \"" + job.getTitle() + "\"");
        return payment;
    }

    // UC08 — Provider settles at seeker's proposed (challenged) amount
    public Payment settleAtProposedAmount(int invoiceId, double proposedAmount) throws Exception {
        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) throw new Exception("Invoice not found.");

        invoice.setTotalAmount(proposedAmount);
        invoiceDAO.saveInvoice(invoice);

        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setAmount(proposedAmount);
        payment.setPaymentMethod("SIMULATED");

        int id = paymentDAO.insertPayment(payment);
        payment.setPaymentId(id);

        // Resolve dispute
        Dispute dispute = disputeDAO.getDisputeByInvoiceId(invoiceId);
        if (dispute != null) disputeDAO.resolveDispute(dispute.getDisputeId());

        jobDAO.updateJobStatus(invoice.getJobId(), MicroJob.STATUS_SETTLED);

        MicroJob job = jobDAO.getJobById(invoice.getJobId());
        int seekerId = getHiredSeekerId(job.getJobId());
        notifier.notifySeeker(seekerId,
                "✅ Payment of PKR " + String.format("%.2f", proposedAmount)
                        + " (negotiated) received for: \"" + job.getTitle() + "\"");
        return payment;
    }

    // UC08 — Provider raises initial dispute
    // KEY FIX: sets job status back to IN_PROGRESS so the seeker can see
    // it in Complete Job and respond via Challenge Dispute.
    public Dispute disputeInvoice(int invoiceId, String reason) throws Exception {
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("Dispute reason cannot be empty.");

        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) throw new Exception("Invoice not found.");

        Dispute dispute = new Dispute();
        dispute.setInvoiceId(invoiceId);
        dispute.setReason(reason.trim());
        dispute.setResolutionStatus(Dispute.STATUS_OPEN);
        dispute.setRaisedBy(Dispute.RAISED_BY_PROVIDER);
        dispute.setRoundCount(0);

        int id = disputeDAO.insertDispute(dispute);
        dispute.setDisputeId(id);

        // Set job back to IN_PROGRESS so seeker sees it in Complete Job screen
        jobDAO.updateJobStatus(invoice.getJobId(), MicroJob.STATUS_IN_PROGRESS);

        // Notify seeker
        MicroJob job = jobDAO.getJobById(invoice.getJobId());
        int seekerId = getHiredSeekerId(job.getJobId());
        notifier.notifySeeker(seekerId,
                "⚠️ Provider disputed your invoice for: \"" + job.getTitle()
                        + "\". Reason: " + reason.trim()
                        + ". Go to Complete Job → Challenge Dispute to respond.");

        return dispute;
    }

    // Seeker challenges dispute with counter-argument + proposed amount
    public void challengeDispute(int disputeId, String challenge,
                                 double proposedAmount) throws Exception {
        if (challenge == null || challenge.trim().isEmpty())
            throw new IllegalArgumentException("Challenge argument cannot be empty.");
        if (proposedAmount <= 0)
            throw new IllegalArgumentException("Proposed amount must be greater than zero.");

        Dispute dispute = disputeDAO.getDisputeById(disputeId);
        if (dispute == null) throw new Exception("Dispute not found.");
        if (dispute.isMaxRoundsReached())
            throw new Exception("Maximum dispute rounds reached.");

        disputeDAO.challengeDispute(disputeId, challenge.trim(), proposedAmount);

        // Set job back to PENDING_PAYMENT so it appears in provider's Review Invoice
        Invoice invoice = invoiceDAO.getInvoiceById(dispute.getInvoiceId());
        jobDAO.updateJobStatus(invoice.getJobId(), MicroJob.STATUS_PENDING_PAYMENT);

        // Notify provider
        MicroJob job = jobDAO.getJobById(invoice.getJobId());
        notifier.notifyProvider(job.getProviderId(),
                "💬 Seeker challenged your dispute for: \"" + job.getTitle()
                        + "\". Proposed: PKR " + String.format("%.2f", proposedAmount)
                        + ". Go to Review Invoice to respond.");
    }

    // Provider re-disputes after seeing seeker's challenge
    public void reDispute(int invoiceId, String newReason) throws Exception {
        if (newReason == null || newReason.trim().isEmpty())
            throw new IllegalArgumentException("Reason cannot be empty.");

        Dispute dispute = disputeDAO.getDisputeByInvoiceId(invoiceId);
        if (dispute == null) throw new Exception("No dispute found for this invoice.");

        if (dispute.isMaxRoundsReached()) {
            // Auto-resolve in seeker's favour after max rounds
            settleAtProposedAmount(invoiceId, dispute.getProposedAmount());
            return;
        }

        disputeDAO.reDispute(dispute.getDisputeId(), newReason.trim());

        // Set job back to IN_PROGRESS so seeker can see it again
        jobDAO.updateJobStatus(invoiceId, MicroJob.STATUS_IN_PROGRESS);

        // Notify seeker
        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        MicroJob job = jobDAO.getJobById(invoice.getJobId());
        int seekerId = getHiredSeekerId(job.getJobId());
        notifier.notifySeeker(seekerId,
                "⚠️ Provider re-disputed your challenge for: \"" + job.getTitle()
                        + "\". New reason: " + newReason.trim()
                        + ". Go to Complete Job to respond.");
    }

    public Dispute getDisputeByInvoiceId(int invoiceId) throws Exception {
        return disputeDAO.getDisputeByInvoiceId(invoiceId);
    }

    private int getHiredSeekerId(int jobId) throws Exception {
        ApplicationDAO appDAO = new ApplicationDAO();
        return appDAO.getAcceptedSeekerId(jobId);
    }
}