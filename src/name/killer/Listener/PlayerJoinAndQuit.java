package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import name.killer.Killer;

public class PlayerJoinAndQuit implements Listener {

    @EventHandler
    public void onPJE(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (Killer.getInstance().ispalying(player)) {
                Killer.getInstance().delpalying(player);
            }
        }
    }

    @EventHandler
    public void onPQE(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player != null){
            if (Killer.getInstance().ispalying(player)){
                Killer.getInstance().delpalying(player);
            }
        }
    }

}
