package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import name.killer.Killer;
import name.killer.Room.Room;


public class PlayerJoinAndQuit implements Listener {

    @EventHandler
    public void onPJE(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Room room : Killer.getInstance().getRooms().values()) {
            if (player != null && room.isPlaying(player)) {
                room.quitRoom(player);
            }
        }
    }

    @EventHandler
    public void onPQE(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Room room : Killer.getInstance().getRooms().values()) {
            if (player != null && room.isPlaying(player)) {
                room.quitRoom(player);
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
            if (Killer.getInstance().getRooms().containsKey(fromLevel)) {
                Killer.getInstance().getRooms().get(fromLevel).quitRoom(player);
            }
            if (!player.isOp() && Killer.getInstance().getRooms().containsKey(toLevel) &&
                    !Killer.getInstance().getRooms().get(toLevel).isPlaying(player)) {
                event.setCancelled();
                player.sendMessage("要进入游戏地图，请先加入游戏！");
            }
        }
    }

}
