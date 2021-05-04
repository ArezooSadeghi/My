package com.example.sipsupporterapp.eventbus;

public class PostSelectedPaymentSubjectEvent {

    private String paymentSubject;
    private int paymentSubjectID;

    public PostSelectedPaymentSubjectEvent(String paymentSubject, int paymentSubjectID) {
        this.paymentSubject = paymentSubject;
        this.paymentSubjectID = paymentSubjectID;
    }

    public String getPaymentSubject() {
        return paymentSubject;
    }

    public void setPaymentSubject(String paymentSubject) {
        this.paymentSubject = paymentSubject;
    }

    public int getPaymentSubjectID() {
        return paymentSubjectID;
    }

    public void setPaymentSubjectID(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }
}
