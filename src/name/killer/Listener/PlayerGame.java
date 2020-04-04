package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import name.killer.Killer;
import name.killer.Room.Room;

public class PlayerGame implements Listener {

    @EventHandler
    public void onEDBE(EntityDamageByEntityEvent event) {
        //damager 攻击者 entity 被攻击者
        if (event.getDamager() == null || event.getEntity() == null) { return; }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            if (player1 == null || player2 == null) { return; }
            Room gameRoom = Killer.getInstance().getRooms().get(player1.getLevel().getName());
            if (gameRoom.getPlayerMode(player1) == 2) {
                if (player1.getInventory().getItemInHand().getId() == 267) {
                    player1.sendMessage("你成功击杀了一位玩家");
                    player2.setGamemode(3);
                    gameRoom.addPlaying(player2, 3);
                    //Killer.getInstance().setPlayerInvisible(player2, true);
                    player2.sendMessage("你被杀手杀死了");
                }
            }
        }
        event.setCancelled();
    }

    @EventHandler
    public void onEDBEE(EntityDamageByChildEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null) { return; }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player1 = ((Player) event.getDamager()).getPlayer();
            Player player2 = ((Player) event.getEntity()).getPlayer();
            Room gameRoom = Killer.getInstance().getRooms().get(player1.getLevel().getName());
            if (gameRoom.getPlayerMode(player1) == 1) {
                if (gameRoom.getPlayerMode(player2) == 2) {
                    if (event.getChild().getId() == 262) {
                        gameRoom.endGame(1);
                    }
                }
            }

        }
    }

}
