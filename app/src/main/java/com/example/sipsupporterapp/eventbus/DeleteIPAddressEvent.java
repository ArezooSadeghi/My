package com.example.sipsupporterapp.eventbus;

import com.example.sipsupporterapp.model.ServerData;

public class DeleteIPAddressEvent {
    private ServerData serverData;

    public DeleteIPAddressEvent(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }
}
