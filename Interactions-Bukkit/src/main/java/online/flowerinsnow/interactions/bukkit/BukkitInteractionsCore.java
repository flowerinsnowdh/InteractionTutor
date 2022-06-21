package online.flowerinsnow.interactions.bukkit;

import online.flowerinsnow.interactions.IInteractions;
import online.flowerinsnow.interactions.bukkit.database.BukkitSQLManager;
import online.flowerinsnow.interactions.bukkit.redis.BukkitRedisManager;
import online.flowerinsnow.interactions.bukkit.server.BukkitServersManager;
import online.flowerinsnow.interactions.server.IServersManager;
import org.jetbrains.annotations.NotNull;

public class BukkitInteractionsCore implements IInteractions {
    private final BukkitSQLManager sqlManager;
    private final BukkitRedisManager redisManager;
    private final BukkitServersManager serversManager;

    public BukkitInteractionsCore(BukkitSQLManager sqlManager, BukkitRedisManager redisManager, BukkitServersManager serversManager) {
        this.sqlManager = sqlManager;
        this.redisManager = redisManager;
        this.serversManager = serversManager;
    }

    @Override
    public @NotNull BukkitSQLManager getSQLManager() {
        return sqlManager;
    }

    @NotNull
    @Override
    public BukkitRedisManager getRedisManager() {
        return redisManager;
    }

    @Override
    public @NotNull BukkitServersManager getServersManager() {
        return serversManager;
    }
}
