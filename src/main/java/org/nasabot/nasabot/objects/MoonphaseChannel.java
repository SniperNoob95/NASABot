package org.nasabot.nasabot.objects;

public class MoonphaseChannel {
    private final String serverId;
    private final String channelId;

    public MoonphaseChannel(String serverId, String channelId) {
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
