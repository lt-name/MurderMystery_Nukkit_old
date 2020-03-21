package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.level.Level;
import name.killer.Killer;
import name.killer.Room.GameRoom;



public class PlayerGame implements Listener {

/*    @EventHandler
    public void onEDE(EntityDamageEvent event) {
        //damager 攻击者 entity 被攻击者
        if (event == null) { return; }

        if (event.getEntity() instanceof Player) {
            Player player2 = ((Player) event.getEntity()).getPlayer();
            //实体对实体伤害
            if (event instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if (damager instanceof Player) {
                    Player player1 = ((Player) damager).getPlayer();
                }
            }else if (event instanceof EntityDamageByChildEntityEvent) {
                Entity damager = ((EntityDamageByChildEntityEvent) event).getDamager();

            }
        }

    }*/

    @EventHandler
    public void onEDBE(EntityDamageByEntityEvent event) {
        //damager 攻击者 entity 被攻击者
        if (event.getDamager() == null || event.getEntity() == null) { return; }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            if (player1 == null || player2 == null) { return; }
            GameRoom gameRoom = Killer.getInstance().getRooms().get(player1.getLevel().getName());
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
            GameRoom gameRoom = Killer.getInstance().getRooms().get(player1.getLevel().getName());
            if (gameRoom.getPlayerMode(player1) == 1) {
                if (gameRoom.getPlayerMode(player2) == 2) {
                    if (event.getChild().getId() == 262) {
                        gameRoom.endGame(1);
                    }
                }
            }

        }
    }

    //准备状态的玩家 游戏地图 禁止放置或破坏
    @EventHandler
    public void onBPE(BlockPlaceEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onBBE(BlockBreakEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onEEE(EntityExplodeEvent event) {
        Level level = event.getEntity().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

}
