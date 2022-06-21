package online.flowerinsnow.interactions.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import online.flowerinsnow.interactions.InteractionsAPI;
import online.flowerinsnow.interactions.bungee.database.BungeeSQLManager;
import online.flowerinsnow.interactions.bungee.listener.JoinLeftListener;
import online.flowerinsnow.interactions.bungee.redis.BungeeRedisManager;
import online.flowerinsnow.interactions.bungee.redis.BungeeInteractionsRedisListener;
import online.flowerinsnow.interactions.bungee.server.BungeeServersManager;
import online.flowerinsnow.interactions.bungee.util.IOUtils;
import online.flowerinsnow.interactions.redis.IRedisManager;

import java.io.File;
import java.io.IOException;

public class Main extends Plugin {
    private static Main instance;
    private Configuration config;
    private static BungeeInteractionsCore core;
    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        BungeeSQLManager sqlManager = new BungeeSQLManager();
        if (!sqlManager.connect(
                getConfig().getString("sql.url"), getConfig().getString("sql.username"), getConfig().getString("sql.password"),
                getConfig().getInt("sql.initSize"), getConfig().getInt("sql.maxActive"), getConfig().getLong("sql.maxWait")
        )) {
            getLogger().severe("数据库连接失败，请检查数据库配置");
        }

        BungeeRedisManager redisManager = new BungeeRedisManager(getConfig().getString("server_name"));
        redisManager.connect(getConfig().getString("redis.host"), getConfig().getInt("redis.port"), getConfig().getString("redis.password"));
        redisManager.subscribeService("Interactions");
        redisManager.addListener(new BungeeInteractionsRedisListener());

        BungeeServersManager serversManager = new BungeeServersManager(redisManager);

        core = new BungeeInteractionsCore(sqlManager, redisManager, serversManager);
        InteractionsAPI.setInstance(core);

        getProxy().getPluginManager().registerListener(this, new JoinLeftListener());
    }

    @Override
    public void onDisable() {
        if (core != null) {
            IRedisManager redisManager = core.getRedisManager();
            redisManager.getConnection().sync().hdel("interactions:servers", redisManager.getCurrentServerName());
            redisManager.getConnection().sync().del("interactions:playerlist:" + redisManager.getCurrentServerName());
            redisManager.publish("Interactions", "server_offline");
        }
        getProxy().getScheduler().cancel(this);
        getCore().getSQLManager().disconnect();
        getCore().getRedisManager().disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public static BungeeInteractionsCore getCore() {
        return core;
    }

    public void saveDefaultConfig() {
        IOUtils.copy(getResourceAsStream("config.yml"), true, new File(getDataFolder(), "config.yml"));
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Configuration getConfig() {
        return config;
    }
}
