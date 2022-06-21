package online.flowerinsnow.interactions.server;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * 代表一个本地服务器的信息
 */
public interface IServerInfo {
    /**
     * 是否是代理服务器(BugneeCord)
     *
     * @return 是否是代理服务器(BugneeCord)
     */
    boolean isProxy();

    /**
     * 获取服务器名
     *
     * @return 服务器名
     */
    @NotNull String getName();

    /**
     * 获取当前服务器的玩家列表
     *
     * @return 当前服务器的玩家列表
     */
    @NotNull Set<UUID> getServerPlayerList();
}
