package online.flowerinsnow.interactions.bukkit.listener;

import online.flowerinsnow.interactions.bukkit.Main;
import online.flowerinsnow.interactions.redis.IRedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeftListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            IRedisManager redisManager = Main.getCore().getRedisManager();
            String uuid = e.getPlayer().getUniqueId().toString();
            redisManager.getConnection().sync().sadd("interactions:playerlist:" + redisManager.getCurrentServerName(), uuid);
            redisManager.publish("Interactions", "player_join", uuid);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            IRedisManager redisManager = Main.getCore().getRedisManager();
            String uuid = e.getPlayer().getUniqueId().toString();
            redisManager.getConnection().sync().srem("interactions:playerlist:" + redisManager.getCurrentServerName(), uuid);
            redisManager.publish("Interactions", "player_leave", uuid);
        });
    }
}
