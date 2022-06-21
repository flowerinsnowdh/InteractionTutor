package online.flowerinsnow.interactions.bukkit.server;

import online.flowerinsnow.interactions.redis.IRedisManager;
import online.flowerinsnow.interactions.server.IServerInfo;
import online.flowerinsnow.interactions.server.IServersManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BukkitServersManager implements IServersManager {
    public final Set<IServerInfo> servers = new HashSet<>();

    public BukkitServersManager(IRedisManager redisManager) {

        // 初始化服务器列表
        Map<String, String> value = redisManager.getConnection().sync().hgetall("interactions:servers");
        if (value == null) {
            value = new HashMap<>();
            redisManager.getConnection().sync().hset("interactions:servers", value);
        }
        value.put(redisManager.getCurrentServerName(), "false");
        // 将此服务器公布到Redis仓库
        redisManager.getConnection().sync().hset("interactions:servers", redisManager.getCurrentServerName(), "false");

        // 将此服务器的在线玩家公布到服务器仓库
        HashSet<String> uuids = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(p -> uuids.add(p.getUniqueId().toString()));
        String[] array = new String[uuids.size()];
        uuids.toArray(array);
        redisManager.getConnection().sync().sadd("interactions:playerlist:" + redisManager.getCurrentServerName(), array);

        // 开始加载所有服务器
        HashSet<IServerInfo> servers = new HashSet<>();
        value.forEach((k, v) -> {
            Set<String> playerList = redisManager.getConnection().sync().smembers("interactions:playerlist:" + k);
            Set<UUID> playerUUIDS = new HashSet<>();
            playerList.forEach(p -> playerUUIDS.add(UUID.fromString(p)));
            servers.add(new BukkitServerInfo("true".equalsIgnoreCase(v), k, playerUUIDS));
        });
        BukkitServersManager.this.servers.addAll(servers);

        // 向所有的其他在线服务器报告我们的存在
        redisManager.publish("Interactions", "server_online", "false");
    }

    @Override
    public @NotNull Set<IServerInfo> getOnlineServerList() {
        return new HashSet<>(this.servers);
    }

    @Override
    public void sendPlayerToServer(@NotNull UUID player, IServerInfo server) {

    }
}
