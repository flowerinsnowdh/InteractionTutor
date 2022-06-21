package online.flowerinsnow.interactions.bungee.redis;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import online.flowerinsnow.interactions.bungee.Main;
import online.flowerinsnow.interactions.bungee.server.BungeeServerInfo;
import online.flowerinsnow.interactions.redis.IRedisListener;
import online.flowerinsnow.interactions.server.IServerInfo;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BungeeInteractionsRedisListener implements IRedisListener {
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
                    BungeeServerInfo info = new BungeeServerInfo(proxy, from, players);
                    Main.getCore().getServersManager().servers.add(info);
                }
                case "server_offline" -> {
                    if (from.equals(Main.getCore().getRedisManager().getCurrentServerName())) return; // 不处理自己
                    Main.getCore().getServersManager().servers.removeIf(info -> info.getName().equals(from));
                }
                case "player_join" -> {
                    Set<IServerInfo> onlineServers = Main.getCore().getServersManager().getOnlineServerList();
                    onlineServers.forEach(s -> {
                        BungeeServerInfo serverInfo = (BungeeServerInfo) s;
                        if (s.getName().equals(from)) {
                            serverInfo.serverPlayerList.add(UUID.fromString(messages[1]));
                        }
                    });
                }
                case "player_leave" -> {
                    Set<IServerInfo> onlineServers = Main.getCore().getServersManager().getOnlineServerList();
                    onlineServers.forEach(s -> {
                        BungeeServerInfo serverInfo = (BungeeServerInfo) s;
                        if (s.getName().equals(from)) {
                            serverInfo.serverPlayerList.remove(UUID.fromString(messages[1]));
                        }
                    });
                }
                case "server_teleport" -> {
                    UUID uuid = UUID.fromString(messages[1]);
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                    if (player == null) return;
                    ServerInfo info = ProxyServer.getInstance().getServerInfo(messages[2]);
                    if (info == null) return;
                    player.connect(info);
                }
                case "player_shout" -> {
                    String player = messages[1];
                    String context = new String(Base64.getDecoder().decode(messages[2]), StandardCharsets.UTF_8);
                    ProxyServer.getInstance().broadcast(new TextComponent("<" + player + "> 喊话：" + context));
                }
            }
        }
    }
}
