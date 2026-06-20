package com.quickhire.model;

import java.time.LocalDateTime;

public class Dispute {
    private int    disputeId;
    private int    invoiceId;
    private String reason;
    private LocalDateTime raisedDate;
    private String resolutionStatus;
    private int    roundCount;
    private String raisedBy;
    private String lastChallenge;
    private Double proposedAmount;

    public static final String STATUS_OPEN          = "OPEN";
    public static final String STATUS_RESOLVED      = "RESOLVED";
    public static final String STATUS_CHALLENGED    = "CHALLENGED";   // seeker pushed back
    public static final String STATUS_RE_DISPUTED   = "RE_DISPUTED";  // provider pushed back again
    public static final String RAISED_BY_PROVIDER   = "PROVIDER";
    public static final String RAISED_BY_SEEKER     = "SEEKER";

    // Hidden max rounds — not shown in UI
    public static final int MAX_ROUNDS = 3;

    public Dispute() {
        this.resolutionStatus = STATUS_OPEN;
        this.roundCount       = 0;
        this.raisedBy         = RAISED_BY_PROVIDER;
    }

    // Getters & Setters
    public int    getDisputeId()                    { return disputeId; }
    public void   setDisputeId(int id)              { this.disputeId = id; }
    public int    getInvoiceId()                    { return invoiceId; }
    public void   setInvoiceId(int id)              { this.invoiceId = id; }
    public String getReason()                       { return reason; }
    public void   setReason(String r)               { this.reason = r; }
    public LocalDateTime getRaisedDate()            { return raisedDate; }
    public void   setRaisedDate(LocalDateTime d)    { this.raisedDate = d; }
    public String getResolutionStatus()             { return resolutionStatus; }
    public void   setResolutionStatus(String s)     { this.resolutionStatus = s; }
    public int    getRoundCount()                   { return roundCount; }
    public void   setRoundCount(int r)              { this.roundCount = r; }
    public String getRaisedBy()                     { return raisedBy; }
    public void   setRaisedBy(String r)             { this.raisedBy = r; }
    public String getLastChallenge()                { return lastChallenge; }
    public void   setLastChallenge(String c)        { this.lastChallenge = c; }
    public Double getProposedAmount()               { return proposedAmount; }
    public void   setProposedAmount(Double a)       { this.proposedAmount = a; }

    /** True when it's the seeker's turn to respond */
    public boolean isAwaitingSeekerResponse() {
        return STATUS_OPEN.equals(resolutionStatus)
                || STATUS_RE_DISPUTED.equals(resolutionStatus);
    }

    /** True when it's the provider's turn to respond */
    public boolean isAwaitingProviderResponse() {
        return STATUS_CHALLENGED.equals(resolutionStatus);
    }

    /** True when the back-and-forth has hit the max limit */
    public boolean isMaxRoundsReached() {
        return roundCount >= MAX_ROUNDS;
    }
}