package online.flowerinsnow.interactions;

import online.flowerinsnow.interactions.database.ISQLManager;
import online.flowerinsnow.interactions.redis.IRedisManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InteractionsAPI {
    private static IInteractions instance;

    public static @NotNull IInteractions getInstance() {
        return instance;
    }

    public static void setInstance(@NotNull IInteractions instance) {
        Objects.requireNonNull(instance);
        if (InteractionsAPI.instance != null) {
            throw new IllegalArgumentException("Instance already set.");
        }
    }

    /**
     * 获取一个数据库连接
     *
     * @return 一个数据库连接
     */
    public static @NotNull ISQLManager getSQLManager() {
        return getInstance().getSQLManager();
    }

    /**
     * 获取Redis管理器
     *
     * @return Redis管理器
     */
    public static @NotNull IRedisManager getRedisManager() {
        return getInstance().getRedisManager();
    }
}
