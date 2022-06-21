package online.flowerinsnow.interactions;

import online.flowerinsnow.interactions.database.ISQLManager;
import online.flowerinsnow.interactions.redis.IRedisManager;
import online.flowerinsnow.interactions.server.IServersManager;
import org.jetbrains.annotations.NotNull;

public interface IInteractions {
    /**
     * 获取SQL管理器
     *
     * @return SQL管理器
     */
    @NotNull ISQLManager getSQLManager();

    /**
     * 获取Redis管理器
     *
     * @return Redis管理器
     */
    @NotNull IRedisManager getRedisManager();

    /**
     * 获取群组服的所有在线服务器
     *
     * @return 所有在线服务器
     */
    @NotNull IServersManager getServersManager();
}
