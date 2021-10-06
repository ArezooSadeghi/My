package com.example.sipsupporterapp.eventbus;

public class NewRefreshEvent {

    private int attachID;

    public NewRefreshEvent(int attachID) {
        this.attachID = attachID;
    }

    public int getAttachID() {
        return attachID;
    }

    public void setAttachID(int attachID) {
        this.attachID = attachID;
    }
}
