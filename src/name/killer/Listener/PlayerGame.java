package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import name.killer.Killer;


public class PlayerGame implements Listener {

    @EventHandler
    public void onEDBE(EntityDamageByEntityEvent event) {
        //damager 攻击者 entity 被攻击者
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            if (player1.getInventory().getItemInHand().getId() == 267) {
                player1.sendMessage("你成功击杀了一位玩家");
                player2.setGamemode(3);
                Killer.getInstance().setPlayerInvisible(player2, true);
                player2.sendMessage("你被杀手杀死了");
            }
        }
    }

    //游戏地图禁止破坏
    @EventHandler
    public void onBPE(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && Killer.getInstance().ispalying(player)) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onBBE(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && Killer.getInstance().ispalying(player)) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onEEE(EntityExplodeEvent event) {
        String level = event.getEntity().getLevel().getName();
        if ((level != null) && (Killer.getInstance().getWorld().equals(level))) {
            event.setCancelled();
        }
    }

}
