package online.flowerinsnow.interactions.bukkit.redis;

import online.flowerinsnow.interactions.bukkit.Main;
import online.flowerinsnow.interactions.bukkit.server.BukkitServerInfo;
import online.flowerinsnow.interactions.redis.IRedisListener;
import online.flowerinsnow.interactions.server.IServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BukkitInteractionsRedisListener implements IRedisListener {
    @Override
    public void onMessage(@NotNull String from, @NotNull String service, @NotNull String... messages) {
        if ("Interactions".equals(service)) {
            switch (messages[0]) {
                case "server_online" -> { // 某服务器上线
                    if (from.equals(Main.getCore().getRedisManager().getCurrentServerName())) return; // 不处理自己
                    boolean proxy = "true".equalsIgnoreCase(messages[1]);
                    Set<UUID> players = new HashSet<>();
                    Main.getCore().getRedisManager().getConnection().sync().smembers("interactions:playerlist:" + from).forEach(p ->
                            players.add(UUID.fromString(p))
                    );

                    // 缓存到本地
                    BukkitServerInfo info = new BukkitServerInfo(proxy, from, players);
                    Main.getCore().getServersManager().servers.add(info);
                }
                case "server_offline" -> {
                    if (from.equals(Main.getCore().getRedisManager().getCurrentServerName())) return; // 不处理自己
                    Main.getCore().getServersManager().servers.removeIf(info -> info.getName().equals(from));
                }
                case "player_join" -> {
                    Set<IServerInfo> onlineServers = Main.getCore().getServersManager().getOnlineServerList();
                    onlineServers.forEach(s -> {
                        BukkitServerInfo serverInfo = (BukkitServerInfo) s;
                        if (s.getName().equals(from)) {
                            serverInfo.serverPlayerList.add(UUID.fromString(messages[1]));
                        }
                    });
                }
                case "player_leave" -> {
                    Set<IServerInfo> onlineServers = Main.getCore().getServersManager().getOnlineServerList();
                    onlineServers.forEach(s -> {
                        BukkitServerInfo serverInfo = (BukkitServerInfo) s;
                        if (s.getName().equals(from)) {
                            serverInfo.serverPlayerList.remove(UUID.fromString(messages[1]));
                        }
                    });
                }
            }
        }
    }
}
