package name.KillerGame.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.player.PlayerBlockPickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import name.KillerGame.KillerGame;
import name.KillerGame.Room.Room;

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
            Room gameRoom = KillerGame.getInstance().getRooms().get(player1.getLevel().getName());
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
            Room room = KillerGame.getInstance().getRooms().get(player1.getLevel().getName());
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

    /**
     * 玩家拾取物品事件
     * @param event 事件
     */
    @EventHandler
    public void onPick(PlayerBlockPickEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (player != null && item != null && item.getId() == 266) {
            if (!KillerGame.getInstance().getRooms().containsKey(player.getLevel().getName())) {
                return;
            }
            KillerGame.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    int j = 0;
                    boolean bow = false;
                    for (Item item : player.getInventory().getContents().values()) {
                        if (item.getId() == 266) {
                            j += item.getCount();
                            continue;
                        }
                        if (item.getId() == 261) {
                            bow = true;
                        }
                    }
                    if (j > 9) {
                        player.getInventory().removeItem(Item.get(266, 0, 10));
                        player.getInventory().addItem(Item.get(262, 0, 1));
                        if (bow) {
                            player.getInventory().addItem(Item.get(261, 0, 1));
                        }
                    }
                }
            }, 10);
        }
    }

    /**
     * 生命实体射出箭 事件
     * @param event 事件
     */
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            if (!KillerGame.getInstance().getRooms().containsKey(player.getLevel().getName())) {
                return;
            }
            if (KillerGame.getInstance().getRooms().get(player.getLevel().getName()).getPlayers().get(player) == 2) {
                player.getInventory().addItem(Item.get(262, 0, 1));
                return;
            }
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
    }

}
