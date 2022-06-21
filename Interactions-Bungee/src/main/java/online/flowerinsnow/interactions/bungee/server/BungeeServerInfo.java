package online.flowerinsnow.interactions.bungee.server;

import online.flowerinsnow.interactions.server.IServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BungeeServerInfo implements IServerInfo {
    public final boolean proxy;
    public final String name;
    public final Set<UUID> serverPlayerList = new HashSet<>();

    public BungeeServerInfo(boolean proxy, String name, Set<UUID> players) {
        this.proxy = proxy;
        this.name = name;
        this.serverPlayerList.addAll(players);
    }

    @Override
    public boolean isProxy() {
        return proxy;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Set<UUID> getServerPlayerList() {
        return new HashSet<>(serverPlayerList);
    }
}
