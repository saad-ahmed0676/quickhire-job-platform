package com.quickhire.model;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int jobId;
    private double hoursWorked;
    private double supplyCost;
    private double totalAmount;
    private LocalDateTime dateGenerated;
    private boolean confirmedBySeeker;

    // Transient — needed for display
    private double hourlyRate;  // pulled from MicroJob

    public Invoice() {}

    public Invoice(int invoiceId, int jobId, double hoursWorked, double supplyCost,
                   double totalAmount, LocalDateTime dateGenerated, boolean confirmedBySeeker) {
        this.invoiceId = invoiceId;
        this.jobId = jobId;
        this.hoursWorked = hoursWorked;
        this.supplyCost = supplyCost;
        this.totalAmount = totalAmount;
        this.dateGenerated = dateGenerated;
        this.confirmedBySeeker = confirmedBySeeker;
    }

    // Business logic method (Information Expert — Invoice knows how to calc its total)
    public double calculateTotal(double hourlyRate) {
        return (hoursWorked * hourlyRate) + supplyCost;
    }

    // Getters & Setters
    public int getInvoiceId()                       { return invoiceId; }
    public void setInvoiceId(int id)                { this.invoiceId = id; }
    public int getJobId()                           { return jobId; }
    public void setJobId(int jobId)                 { this.jobId = jobId; }
    public double getHoursWorked()                  { return hoursWorked; }
    public void setHoursWorked(double h)            { this.hoursWorked = h; }
    public double getSupplyCost()                   { return supplyCost; }
    public void setSupplyCost(double c)             { this.supplyCost = c; }
    public double getTotalAmount()                  { return totalAmount; }
    public void setTotalAmount(double t)            { this.totalAmount = t; }
    public LocalDateTime getDateGenerated()         { return dateGenerated; }
    public void setDateGenerated(LocalDateTime d)   { this.dateGenerated = d; }
    public boolean isConfirmedBySeeker()            { return confirmedBySeeker; }
    public void setConfirmedBySeeker(boolean b)     { this.confirmedBySeeker = b; }
    public double getHourlyRate()                   { return hourlyRate; }
    public void setHourlyRate(double r)             { this.hourlyRate = r; }
}