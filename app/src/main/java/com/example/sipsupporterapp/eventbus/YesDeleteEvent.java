package com.example.sipsupporterapp.eventbus;

public class YesDeleteEvent {

    private boolean yesDelete;

    public YesDeleteEvent(boolean yesDelete) {
        this.yesDelete = yesDelete;
    }

    public boolean isYesDelete() {
        return yesDelete;
    }

    public void setYesDelete(boolean yesDelete) {
        this.yesDelete = yesDelete;
    }
}
