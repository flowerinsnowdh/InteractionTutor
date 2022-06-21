package online.flowerinsnow.interactions.bukkit.command;

import online.flowerinsnow.interactions.InteractionsAPI;
import online.flowerinsnow.interactions.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ServerTeleportCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("interactions.serverteleport")) {
                player.sendMessage("权限不足");
                return true;
            }
            if (args.length == 1) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () ->
                        InteractionsAPI.getInstance().getServersManager().sendPlayerToServer(player.getUniqueId(), args[0]));
            } else {
                player.sendMessage("参数不正确");
            }
        } else {
            sender.sendMessage("只有玩家可以这么做");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            ArrayList<String> list = new ArrayList<>();
            Main.getCore().getServersManager().getOnlineServerList().forEach(server -> {
                if (!server.isProxy()) list.add(server.getName());
            });
            return list;
        } else {
            return new ArrayList<>();
        }
    }
}
