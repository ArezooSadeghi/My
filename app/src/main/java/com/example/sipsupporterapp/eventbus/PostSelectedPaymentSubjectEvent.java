package com.example.sipsupporterapp.eventbus;

public class PostSelectedPaymentSubjectEvent {

    private int paymentSubjectID;

    public PostSelectedPaymentSubjectEvent(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }

    public int getPaymentSubjectID() {
        return paymentSubjectID;
    }

    public void setPaymentSubjectID(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }
}
