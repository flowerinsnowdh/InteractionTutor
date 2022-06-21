package online.flowerinsnow.interactions.bukkit;

import online.flowerinsnow.interactions.InteractionsAPI;
import online.flowerinsnow.interactions.bukkit.command.ServerTeleportCommand;
import online.flowerinsnow.interactions.bukkit.database.BukkitSQLManager;
import online.flowerinsnow.interactions.bukkit.listener.JoinLeftListener;
import online.flowerinsnow.interactions.bukkit.redis.BukkitInteractionsRedisListener;
import online.flowerinsnow.interactions.bukkit.redis.BukkitRedisManager;
import online.flowerinsnow.interactions.bukkit.server.BukkitServersManager;
import online.flowerinsnow.interactions.redis.IRedisManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private static BukkitInteractionsCore core;
    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        BukkitSQLManager sqlManager = new BukkitSQLManager();
        if (!sqlManager.connect(
                getConfig().getString("sql.url"), getConfig().getString("sql.username"), getConfig().getString("sql.password"),
                getConfig().getInt("sql.initSize"), getConfig().getInt("sql.maxActive"), getConfig().getLong("sql.maxWait")
        )) {
            getLogger().severe("数据库连接失败，请检查数据库配置");
        }

        BukkitRedisManager redisManager = new BukkitRedisManager(getConfig().getString("server_name"));
        redisManager.connect(getConfig().getString("redis.host"), getConfig().getInt("redis.port"), getConfig().getString("redis.password"));
        redisManager.subscribeService("Interactions");
        redisManager.addListener(new BukkitInteractionsRedisListener());

        BukkitServersManager serversManager = new BukkitServersManager(redisManager);

        core = new BukkitInteractionsCore(sqlManager, redisManager, serversManager);
        InteractionsAPI.setInstance(core);

        getServer().getPluginManager().registerEvents(new JoinLeftListener(), this);

        registerCommand("serverteleport", new ServerTeleportCommand());
    }

    @Override
    public void onDisable() {
        if (core != null) {
            IRedisManager redisManager = core.getRedisManager();
            redisManager.getConnection().sync().hdel("interactions:servers", redisManager.getCurrentServerName());
            redisManager.getConnection().sync().del("interactions:playerlist:" + redisManager.getCurrentServerName());
            redisManager.publish("Interactions", "server_offline");
        }
        getServer().getScheduler().cancelTasks(this);
        getCore().getSQLManager().disconnect();
        getCore().getRedisManager().disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public static BukkitInteractionsCore getCore() {
        return core;
    }

    private void registerCommand(String name, TabExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }
}
