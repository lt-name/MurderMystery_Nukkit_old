package main.java.name.murdermystery.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.entity.PlayerCorpse;
import main.java.name.murdermystery.event.MurderMysteryPlayerDamage;
import main.java.name.murdermystery.event.MurderMysteryPlayerDeath;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.utils.Tools;

/**
 * 游戏监听器
 */
public class PlayerGame implements Listener {

    /**
     * 玩家被攻击事件(符合游戏条件的攻击)
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDamage(MurderMysteryPlayerDamage event) {
        Player player1 = event.getDamage();
        Player player2 = event.getPlayer();
        Room room = MurderMystery.getInstance().getRooms().get(player2.getLevel().getName());
        //攻击者是杀手
        if (room.getPlayerMode(player1) == 3) {
            player1.sendMessage("§a你成功击杀了一位玩家！");
            player2.sendTitle("§c死亡", "§c你被杀手杀死了", 20, 60, 20);
            if (room.getPlayerMode(player2) == 2) {
                Item item = Item.get(261, 0, 1);
                item.setCustomName("§e侦探之弓");
                room.getWorld().dropItem(player2, item);
            }
        }else { //攻击者是平民或侦探
            if (room.getPlayerMode(player2) == 3) {
                player1.sendMessage("你成功击杀了杀手！");
                player2.sendTitle("§c死亡", "§c你被击杀了", 10, 20, 20);
            } else {
                player1.sendTitle("§c死亡", "§c你击中了队友", 20, 60, 20);
                player2.sendTitle("§c死亡", "§c你被队友打死了", 20, 60, 20);
                MurderMystery.getInstance().getServer().getPluginManager().callEvent(new MurderMysteryPlayerDeath(room, player1));
            }
        }
        MurderMystery.getInstance().getServer().getPluginManager().callEvent(new MurderMysteryPlayerDeath(room, player2));
    }

    /**
     * 玩家死亡事件（游戏中死亡）
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDeath(MurderMysteryPlayerDeath event) {
        Player player = event.getPlayer();
        Room room = event.getRoom();
        room.clearInventory(player);
        player.setGamemode(3);
        room.addPlaying(player, 0);
        if (room.getPlayerMode(player) == 3) {
            return;
        }else if (room.getPlayerMode(player) == 2) {
            Item item = Item.get(261, 0, 1);
            item.setCustomName("§e侦探之弓");
            room.getWorld().dropItem(player, item);
        }
        Tools.addSound(room, Sound.GAME_PLAYER_HURT);
        CompoundTag nbt = Entity.getDefaultNBT(player,
                new Vector3(player.motionX,player.motionY,player.motionZ),(float) player.yaw,(float) player.pitch);
        nbt.putString("NameTag", player.getName()).putFloat("scale",1.0F);
        nbt.putBoolean("isCorpse",true);
        nbt.putCompound("Skin",new CompoundTag()
                .putByteArray("Data", player.getSkin().getSkinData().data)
                .putString("ModelId", player.getSkin().getSkinId()));
        PlayerCorpse entity = new PlayerCorpse(player.getChunk(),nbt);
        entity.setNameTagAlwaysVisible(false);
        entity.setDataProperty(new IntPositionEntityData(28, (int)player.x, (int)player.y, (int)player.z));
        entity.setDataFlag(26, 1, true);
        entity.spawnToAll();
    }


    /**
     * 实体受到另一实体伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Level level = event.getDamager().getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName())) {
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player1 = (Player) event.getDamager();
            Player player2 = (Player) event.getEntity();
            if (player1 == null || player2 == null) {
                return;
            }
            Room room = MurderMystery.getInstance().getRooms().get(player1.getLevel().getName());
            if (room.getPlayerMode(player1) == 3 && player1.getInventory().getItemInHand().getId() == 267) {
                MurderMystery.getInstance().getServer().getPluginManager().callEvent(new MurderMysteryPlayerDamage(player1, player2));
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
            if (room.getPlayerMode(player1) != 3) {
                MurderMystery.getInstance().getServer().getPluginManager().callEvent(new MurderMysteryPlayerDamage(player1, player2));
            }
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
            final Player player = ((Player) event.getEntity()).getPlayer();
            String levelName = player.getLevel().getName();
            if (!MurderMystery.getInstance().getRooms().containsKey(levelName)) {
                return;
            }
            Room room = MurderMystery.getInstance().getRooms().get(levelName);
            if (player.getInventory().getItemInHand().getCustomName().equals("§e侦探之弓")) {
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

    /**
     * 收起掉落的物品时
     * @param event 事件
     */
    @EventHandler
    public void onPickupItem(InventoryPickupItemEvent event) {
        Level level = event.getItem().getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName())) {
            return;
        }
        if (event.getInventory() != null && event.getInventory() instanceof PlayerInventory) {
            Player player = (Player) event.getInventory().getHolder();
            if (event.getItem().getItem().getCustomName().equals("§e侦探之弓") &&
                    MurderMystery.getInstance().getRooms().get(level.getName()).getPlayerMode(player) != 1) {
                event.setCancelled();
            }
        }
    }

}
