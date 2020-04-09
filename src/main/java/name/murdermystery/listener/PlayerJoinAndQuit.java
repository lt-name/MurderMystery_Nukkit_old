package main.java.name.murdermystery.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.scheduler.Task;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.utils.SavePlayerInventory;
import main.java.name.murdermystery.utils.Tools;

import java.util.LinkedHashMap;

/**
 * 玩家进入/退出服务器 或传送到其他世界时，退出房间
 */
public class PlayerJoinAndQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player != null && MurderMystery.getInstance().getRooms().containsKey(player.getLevel().getName())) {
            MurderMystery.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    if (MurderMystery.getInstance().getServer().getOnlinePlayers().containsValue(player)) {
                        Tools.rePlayerState(player ,true);
                        SavePlayerInventory.savePlayerInventory(player, true);
                        player.teleport(MurderMystery.getInstance().getServer().getDefaultLevel().getSafeSpawn());
                    }
                }
            }, 120);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        for (Room room : MurderMystery.getInstance().getRooms().values()) {
            if (room.isPlaying(player)) {
                room.quitRoom(player, false);
            }
        }
    }

    @EventHandler
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String fromLevel = event.getFrom().getLevel().getName();
        String toLevel = event.getTo().getLevel().getName();
        if (player == null || fromLevel == null || toLevel == null) { return; }
        if (!fromLevel.equals(toLevel)) {
            LinkedHashMap<String, Room> room =  MurderMystery.getInstance().getRooms();
            if (room.containsKey(fromLevel) && room.get(fromLevel).isPlaying(player)) {
                event.setCancelled();
                player.sendMessage("§e >> §c退出房间请使用：/killer quit");
            }
            if (!player.isOp() && room.containsKey(toLevel) &&
                    !room.get(toLevel).isPlaying(player)) {
                event.setCancelled();
                player.sendMessage("§e >> §c要进入游戏地图，请先加入游戏！");
            }
        }
    }

}
