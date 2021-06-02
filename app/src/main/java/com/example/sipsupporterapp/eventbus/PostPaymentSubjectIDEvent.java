package com.example.sipsupporterapp.eventbus;

public class PostPaymentSubjectIDEvent {

    private int paymentSubjectID;

    public PostPaymentSubjectIDEvent(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }

    public int getPaymentSubjectID() {
        return paymentSubjectID;
    }

    public void setPaymentSubjectID(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }
}
