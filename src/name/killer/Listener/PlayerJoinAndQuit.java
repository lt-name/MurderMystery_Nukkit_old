package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
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

}
