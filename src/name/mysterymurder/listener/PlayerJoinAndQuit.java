package name.mysterymurder.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.scheduler.Task;
import name.mysterymurder.MysteryMurder;
import name.mysterymurder.room.Room;

/**
 * 玩家进入/退出服务器 或传送到其他世界时，退出房间
 */
public class PlayerJoinAndQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MysteryMurder.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                for (Room room : MysteryMurder.getInstance().getRooms().values()) {
                    if (player != null && room.isPlaying(player)) {
                        room.quitRoom(player, true);
                    }
                }
                if (player != null && MysteryMurder.getInstance().getRooms().containsKey(player.getLevel().getName())) {
                    player.teleport(MysteryMurder.getInstance().getServer().getDefaultLevel().getSafeSpawn());
                }
            }
        }, 100);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Room room : MysteryMurder.getInstance().getRooms().values()) {
            if (player != null && room.isPlaying(player)) {
                room.quitRoom(player, false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String fromLevel = event.getFrom().getLevel().getName();
        String toLevel = event.getTo().getLevel().getName();
        if (player == null || fromLevel == null || toLevel == null) { return; }
        if (!fromLevel.equals(toLevel)) {
            if (MysteryMurder.getInstance().getRooms().containsKey(fromLevel)) {
                Room room = MysteryMurder.getInstance().getRooms().get(fromLevel);
                if (room.isPlaying(player)) {
                    room.quitRoom(player, false);
                }
            }
            if (!player.isOp() && MysteryMurder.getInstance().getRooms().containsKey(toLevel) &&
                    !MysteryMurder.getInstance().getRooms().get(toLevel).isPlaying(player)) {
                event.setCancelled();
                player.sendMessage("§c要进入游戏地图，请先加入游戏！");
            }
        }
    }

}
