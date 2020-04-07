package name.murdermystery.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.Task;
import name.murdermystery.MurderMystery;
import name.murdermystery.room.Room;

/**
 * 游戏监听器
 */
public class PlayerGame implements Listener {

    /**
     * 实体受到另一实体伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null) {
            return;
        }
        Level level = event.getDamager().getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName())) {
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            Room room = MurderMystery.getInstance().getRooms().get(player1.getLevel().getName());
            if (room.getPlayerMode(player1) == 3 && player1.getInventory().getItemInHand().getId() == 267) {
                player1.sendMessage("§a你成功击杀了一位玩家！");
                player2.sendTitle("§c死亡", "§c你被杀手杀死了", 20, 60, 20);
                room.clearInventory(player2);
                player2.setGamemode(3);
                room.addPlaying(player2, 0);
            }
        }
        event.setCancelled();
    }

    /**
     * 实体受到另一个子实体伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onDamageByChild(EntityDamageByChildEntityEvent event) {
        if (event.getDamager() == null || event.getEntity() == null ||
                event.getChild() == null) {
            return;
        }
        Level level = event.getDamager().getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName())) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player1 = ((Player) event.getDamager()).getPlayer();
            Player player2 = ((Player) event.getEntity()).getPlayer();
            if (player1 == player2) {
                return;
            }
            Room room = MurderMystery.getInstance().getRooms().get(player1.getLevel().getName());
            if (room.getPlayerMode(player1) == 3) {
                event.setCancelled();
                return;
            } else if (room.getPlayerMode(player2) == 3) {
                player1.sendMessage("你成功击杀了杀手！");
                player2.sendTitle("§c死亡", "§c你被平民或侦探打死了", 20, 60, 20);
            } else {
                player1.sendTitle("§c死亡", "§c你击中了队友", 20, 60, 20);
                player2.sendTitle("§c死亡", "§c你被队友打死了", 20, 60, 20);
                room.clearInventory(player1);
                player1.setGamemode(3);
                room.addPlaying(player1, 0);
            }
            room.clearInventory(player2);
            player2.setGamemode(3);
            room.addPlaying(player2, 0);
        }
        event.setCancelled();
    }

    /**
     * 生命实体射出箭 事件
     * @param event 事件
     */
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            String levelName = player.getLevel().getName();
            if (!MurderMystery.getInstance().getRooms().containsKey(levelName)) {
                return;
            }
            Room room = MurderMystery.getInstance().getRooms().get(levelName);
            if (room.getPlayerMode(player) == 2) {
                player.getInventory().addItem(Item.get(262, 0, 1));
                return;
            }
            //回收平民的弓
            MurderMystery.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    int j = 0; //箭的数量
                    boolean bow = false;
                    for (Item item : player.getInventory().getContents().values()) {
                        if (item.getId() == 262) {
                            j += item.getCount();
                            continue;
                        }
                        if (item.getId() == 261) {
                            bow = true;
                        }
                    }
                    if (j < 1 && bow) {
                        player.getInventory().removeItem(Item.get(261, 0, 1));
                    }
                }
            }, 20, true);
        }
    }

}
