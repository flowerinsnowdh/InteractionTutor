package online.flowerinsnow.interactions.bungee.server;

import net.md_5.bungee.api.ProxyServer;
import online.flowerinsnow.interactions.redis.IRedisManager;
import online.flowerinsnow.interactions.server.IServerInfo;
import online.flowerinsnow.interactions.server.IServersManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BungeeServersManager implements IServersManager {
    public final Set<IServerInfo> servers = new HashSet<>();

    public BungeeServersManager(IRedisManager redisManager) {

        // 初始化服务器列表
        Map<String, String> value = redisManager.getConnection().sync().hgetall("interactions:servers");
        if (value == null) {
            value = new HashMap<>();
            redisManager.getConnection().sync().hset("interactions:servers", value);
        }
        value.put(redisManager.getCurrentServerName(), "true");
        // 将此服务器公布到Redis仓库
        redisManager.getConnection().sync().hset("interactions:servers", redisManager.getCurrentServerName(), "true");

        // 将此服务器的在线玩家公布到服务器仓库
        HashSet<String> uuids = new HashSet<>();
        ProxyServer.getInstance().getPlayers().forEach(p -> uuids.add(p.getUniqueId().toString()));
        String[] array = new String[uuids.size()];
        uuids.toArray(array);
        redisManager.getConnection().sync().sadd("interactions:playerlist:" + redisManager.getCurrentServerName(), array);

        // 开始加载所有服务器
        HashSet<IServerInfo> servers = new HashSet<>();
        value.forEach((k, v) -> {
            Set<String> playerList = redisManager.getConnection().sync().smembers("interactions:playerlist:" + k);
            Set<UUID> playerUUIDS = new HashSet<>();
            playerList.forEach(p -> playerUUIDS.add(UUID.fromString(p)));
            servers.add(new BungeeServerInfo("true".equalsIgnoreCase(v), k, playerUUIDS));
        });
        BungeeServersManager.this.servers.addAll(servers);

        // 向所有的其他在线服务器报告我们的存在
        redisManager.publish("Interactions", "server_online", "true");
    }

    @Override
    public @NotNull Set<IServerInfo> getOnlineServerList() {
        return new HashSet<>(this.servers);
    }

    @Override
    public void sendPlayerToServer(@NotNull UUID player, IServerInfo server) {

    }
}
