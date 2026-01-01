package org.nasabot.nasabot.objects;

public class APODChannel {
    private final String serverId;
    private final String channelId;

    public APODChannel(String serverId, String channelId) {
        this.serverId = serverId;
        this.channelId = channelId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getChannelId() {
        return channelId;
    }
}
