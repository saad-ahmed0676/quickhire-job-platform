package com.quickhire.model;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int invoiceId;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    public Payment() { this.paymentMethod = "SIMULATED"; }

    public Payment(int paymentId, int invoiceId, double amount,
                   String paymentMethod, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId()                   { return paymentId; }
    public void setPaymentId(int id)            { this.paymentId = id; }
    public int getInvoiceId()                   { return invoiceId; }
    public void setInvoiceId(int id)            { this.invoiceId = id; }
    public double getAmount()                   { return amount; }
    public void setAmount(double amount)        { this.amount = amount; }
    public String getPaymentMethod()            { return paymentMethod; }
    public void setPaymentMethod(String m)      { this.paymentMethod = m; }
    public LocalDateTime getPaymentDate()       { return paymentDate; }
    public void setPaymentDate(LocalDateTime d) { this.paymentDate = d; }
}