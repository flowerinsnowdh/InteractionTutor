package online.flowerinsnow.interactions.server;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * 用于管理所有子服和服务器
 */
public interface IServersManager {
    /**
     * 获取所有在线服务器
     * @return 在线服务器列表
     */
    @NotNull Set<IServerInfo> getOnlineServerList();

    /**
     * 将玩家传送到另一个服务器
     * 因为涉及Redis，调用此方法请异步
     *
     * @param player 玩家UUID
     * @param server 服务器
     */
    void sendPlayerToServer(@NotNull UUID player, String server);
}
