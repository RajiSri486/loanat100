package com.hundreddaysloan.model;

public class PaymentHistory {

    private String loanid;
    private String loan_amount;
    private String emi_due_date;
    private String emi_pay_date;
    private boolean loan_status;

    public PaymentHistory() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public PaymentHistory(String loanid, String loan_amount, String emi_due_date, String emi_pay_date, boolean loan_status) {
        this.loanid = loanid;
        this.loan_amount = loan_amount;
        this.emi_due_date = emi_due_date;
        this.emi_pay_date = emi_pay_date;
        this.loan_status = loan_status;
    }

    public String getLoanId() {
        return loanid;
    }
    public void setLoanId(String loanid) {
        this.loanid = loanid;
    }

    public String getLoanAmount() {
        return loan_amount;
    }
    public void setLoanAmount(String loan_amount) {
        this.loan_amount = loan_amount;
    }
    public String getEMIDueDate() {
        return emi_due_date;
    }
    public void setEMIDueDate(String emi_due_date) {
        this.emi_due_date = emi_due_date;
    }
    public String getEMIPayDate() {
        return emi_pay_date;
    }
    public void setEMIPayDate(String emi_pay_date) {
        this.emi_pay_date = emi_pay_date;
    }
    public boolean getLoanStatus() {
        return loan_status;
    }
    public void setLoanStatus(boolean loan_status) {
        this.loan_status = loan_status;
    }
}
