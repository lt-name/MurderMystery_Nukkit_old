package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import name.killer.Killer;
import name.killer.Room.Room;

/**
 * 游戏监听器
 */
public class PlayerGame implements Listener {

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null) {
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            if (player1 == null || player2 == null) {
                return;
            }
            Room gameRoom = Killer.getInstance().getRooms().get(player1.getLevel().getName());
            if (gameRoom.getPlayerMode(player1) == 2) {
                if (player1.getInventory().getItemInHand().getId() == 267) {
                    player1.sendMessage("你成功击杀了一位玩家！");
                    player2.sendMessage("你被杀手杀死了！");
                    player2.setGamemode(3);
                    gameRoom.addPlaying(player2, 0);
                }
            }
        }
        event.setCancelled();
    }

    @EventHandler
    public void onDamageByChild(EntityDamageByChildEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player1 = ((Player) event.getDamager()).getPlayer();
            Player player2 = ((Player) event.getEntity()).getPlayer();
            if (player1 == null || player2 == null) {
                return;
            }
            Room room = Killer.getInstance().getRooms().get(player1.getLevel().getName());
            if (room.getPlayerMode(player2) == 2 && event.getChild().getId() == 262) {
                player1.sendMessage("你成功击杀了杀手！");
            } else {
                player1.sendMessage("你打中友军啦！");
                player1.setGamemode(3);
                room.addPlaying(player1, 0);
            }
            player2.sendMessage("你已被击杀！");
            player2.setGamemode(3);
            room.addPlaying(player2, 0);
        }
        event.setCancelled();
    }

}
