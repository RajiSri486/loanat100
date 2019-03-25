package com.hundreddaysloan.model;

public class LoanDetails {

    private String loanid;
    private String loan_amount;
    private String loan_start_date;
    private String loan_end_date;
    private boolean loan_status;

    public LoanDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public LoanDetails(String loanid, String loan_amount, String loan_start_date, String loan_end_date, boolean loan_status) {
        this.loanid = loanid;
        this.loan_amount = loan_amount;
        this.loan_start_date = loan_start_date;
        this.loan_end_date = loan_end_date;
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
    public String getLoanStartDate() {
        return loan_start_date;
    }
    public void setLoanStartDate(String loan_start_date) {
        this.loan_start_date = loan_start_date;
    }
    public String getLoanEndDate() {
        return loan_end_date;
    }
    public void setLoanEndDate(String loan_end_date) {
        this.loan_end_date = loan_end_date;
    }
    public boolean getLoanStatus() {
        return loan_status;
    }
    public void setLoanStatus(boolean loan_status) {
        this.loan_status = loan_status;
    }
}
