package online.flowerinsnow.interactions.bungee.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import online.flowerinsnow.interactions.bungee.Main;
import online.flowerinsnow.interactions.redis.IRedisManager;

public class JoinLeftListener implements Listener {
    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), () -> {
            IRedisManager redisManager = Main.getCore().getRedisManager();
            String uuid = e.getPlayer().getUniqueId().toString();
            redisManager.getConnection().sync().sadd("interactions:playerlist:" + redisManager.getCurrentServerName(), uuid);
            redisManager.publish("Interactions", "player_join", uuid);
        });
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), () -> {
            IRedisManager redisManager = Main.getCore().getRedisManager();
            String uuid = e.getPlayer().getUniqueId().toString();
            redisManager.getConnection().sync().srem("interactions:playerlist:" + redisManager.getCurrentServerName(), uuid);
            redisManager.publish("Interactions", "player_leave", uuid);
        });
    }
}
