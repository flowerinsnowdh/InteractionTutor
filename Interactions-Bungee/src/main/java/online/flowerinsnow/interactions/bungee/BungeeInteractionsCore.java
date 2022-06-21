package online.flowerinsnow.interactions.bungee;

import online.flowerinsnow.interactions.IInteractions;
import online.flowerinsnow.interactions.bungee.database.BungeeSQLManager;
import online.flowerinsnow.interactions.bungee.redis.BungeeRedisManager;
import online.flowerinsnow.interactions.bungee.server.BungeeServersManager;
import online.flowerinsnow.interactions.server.IServersManager;
import org.jetbrains.annotations.NotNull;

public class BungeeInteractionsCore implements IInteractions {
    private final BungeeSQLManager sqlManager;
    private final BungeeRedisManager redisManager;
    private final BungeeServersManager serversManager;

    public BungeeInteractionsCore(BungeeSQLManager sqlManager, BungeeRedisManager redisManager, BungeeServersManager serversManager) {
        this.sqlManager = sqlManager;
        this.redisManager = redisManager;
        this.serversManager = serversManager;
    }

    @Override
    public @NotNull BungeeSQLManager getSQLManager() {
        return sqlManager;
    }

    @NotNull
    @Override
    public BungeeRedisManager getRedisManager() {
        return redisManager;
    }

    @Override
    public @NotNull BungeeServersManager getServersManager() {
        return serversManager;
    }
}
