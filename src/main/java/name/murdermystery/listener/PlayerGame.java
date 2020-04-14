package main.java.name.murdermystery.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.entity.PlayerCorpse;
import main.java.name.murdermystery.event.MurderPlayerCorpseSpawnEvent;
import main.java.name.murdermystery.event.MurderPlayerDamageEvent;
import main.java.name.murdermystery.event.MurderPlayerDeathEvent;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.utils.Tools;

import java.util.Random;

/**
 * 游戏监听器
 */
public class PlayerGame implements Listener {

    /**
     * 玩家被攻击事件(符合游戏条件的攻击)
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDamage(MurderPlayerDamageEvent event) {
        Player player1 = event.getDamage();
        Player player2 = event.getPlayer();
        Room room = event.getRoom();
        if (player1 == null || player2 == null || room == null) {
            return;
        }
        //攻击者是杀手
        if (room.getPlayerMode(player1) == 3) {
            player1.sendMessage("§a你成功击杀了一位玩家！");
            player2.sendTitle("§c死亡", "§c你被杀手杀死了", 20, 60, 20);
        }else { //攻击者是平民或侦探
            if (room.getPlayerMode(player2) == 3) {
                player1.sendMessage("你成功击杀了杀手！");
                player2.sendTitle("§c死亡", "§c你被击杀了", 10, 20, 20);
            } else {
                player1.sendTitle("§c死亡", "§c你击中了队友", 20, 60, 20);
                player2.sendTitle("§c死亡", "§c你被队友打死了", 20, 60, 20);
                Server.getInstance().getPluginManager().callEvent(new MurderPlayerDeathEvent(room, player1));
            }
        }
        Server.getInstance().getPluginManager().callEvent(new MurderPlayerDeathEvent(room, player2));
    }

    /**
     * 玩家死亡事件（游戏中死亡）
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDeath(MurderPlayerDeathEvent event) {
        Player player = event.getPlayer();
        Room room = event.getRoom();
        if (player == null || room == null) {
            return;
        }
        room.clearInventory(player);
        player.setGamemode(3);
        room.addPlaying(player, 0);
        Tools.setPlayerInvisible(player, true);
        if (room.getPlayerMode(player) == 2) {
            Item item = Item.get(261, 0, 1);
            item.setCustomName("§e侦探之弓");
            room.getWorld().dropItem(player, item);
        }
        Tools.addSound(room, Sound.GAME_PLAYER_HURT);
        Server.getInstance().getPluginManager().callEvent(new MurderPlayerCorpseSpawnEvent(room, player));
    }

    /**
     * 尸体生成事件
     * @param event 事件
     */
    @EventHandler
    public void onCorpseSpawn(MurderPlayerCorpseSpawnEvent event) {
        Player player = event.getPlayer();
        Room room = event.getRoom();
        if (player == null || room == null) {
            return;
        }
        CompoundTag nbt = PlayerCorpse.getDefaultNBT(player);
        nbt.putCompound("Skin",new CompoundTag()
                .putByteArray("Data", room.getPlayerSkin(player).getSkinData().data)
                .putString("ModelId", room.getPlayerSkin(player).getSkinId()));
        nbt.putFloat("scale", -1.0F);
        PlayerCorpse ent = new PlayerCorpse(player.getChunk(), nbt);
        ent.setSkin(room.getPlayerSkin(player));
        ent.setPosition(new Vector3(player.getFloorX(), this.getCorpseY(player), player.getFloorZ()));
        ent.setGliding(true);
        ent.setRotation(player.getYaw(), 0);
        ent.setImmobile(true);
        ent.spawnToAll();
    }

    /**
     * 获取尸体 Y
     * @param player 玩家
     * @return Y
     */
    private double getCorpseY(Player player) {
        for (int y = 0; y < 10; y++) {
            Level level = player.getLevel();
            if (level.getBlock(player.getFloorX(), player.getFloorY() - y, player.getFloorZ()).getId() != 0) {
                if (level.getBlock(player.getFloorX(), player.getFloorY() - y, player.getFloorZ()).getBoundingBox() != null) {
                    return player.getLevel().getBlock(player.getFloorX(), player.getFloorY() - y, player.getFloorZ()).getBoundingBox().getMaxY() + 0.2;
                }
                return player.getLevel().getBlock(player.getFloorX(), player.getFloorY() - y, player.getFloorZ()).getMinY() + 0.2;
            }
        }
        return player.getFloorY();
    }

    /**
     * 实体受到另一实体伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Level level = event.getDamager().getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
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
                Server.getInstance().getPluginManager().callEvent(new MurderPlayerDamageEvent(room, player1, player2));
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
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player1 = ((Player) event.getDamager()).getPlayer();
            Player player2 = ((Player) event.getEntity()).getPlayer();
            if (player1 == player2) {
                return;
            }
            Room room = MurderMystery.getInstance().getRooms().get(player1.getLevel().getName());
            if (room.getPlayerMode(player1) != 3 && room.getPlayerMode(player1) != 0) {
                Server.getInstance().getPluginManager().callEvent(new MurderPlayerDamageEvent(room, player1, player2));
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
            Player player = ((Player) event.getEntity()).getPlayer();
            if (player == null) {
                return;
            }
            String levelName = player.getLevel().getName();
            if (!MurderMystery.getInstance().getRooms().containsKey(levelName) ||
                    MurderMystery.getInstance().getRooms().get(levelName).getMode() != 2) {
                return;
            }
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
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
            return;
        }
        if (event.getInventory() != null && event.getInventory() instanceof PlayerInventory) {
            Player player = (Player) event.getInventory().getHolder();
            Room room = MurderMystery.getInstance().getRooms().get(player.getLevel().getName());
            if (room.getPlayerMode(player) == 0 || (event.getItem().getItem().getCustomName().equals("§e侦探之弓") &&
                    room.getPlayerMode(player) != 1)) {
                event.setCancelled();
            }
        }
    }

    /**
     * 发送消息事件
     * @param event 事件
     */
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String string = event.getFormat();
        if (player == null || string == null || string.startsWith("/")) {
            return;
        }
        Level level = player.getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName())) {
            return;
        }
        Room room = MurderMystery.getInstance().getRooms().get(player.getLevel().getName());
        if (room.getMode() == 2 && room.getPlayerMode(player) == 0) {
            event.setCancelled(true);
            for (Player p : room.getPlayers().keySet()) {
                if (room.getPlayerMode(p) == 0) {
                    p.sendMessage("§c[死亡] " + player.getName() + "§b >>> " + string);
                }
            }
        }
    }

    /**
     * 玩家手持物品事件
     * @param event 事件
     */
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Item item = player.getInventory().getItemInHand();
        if (item == null) {
            return;
        }
        Level level = player.getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
            return;
        }
        Room room = MurderMystery.getInstance().getRooms().get(player.getLevel().getName());
        if (room.getPlayerMode(player) == 3 && item.getCustomName().equals("§c杀手之剑")) {
            if (room.effectCD < 1) {
                Effect effect = Effect.getEffect(1);
                effect.setAmplifier(1);
                effect.setVisible(false);
                effect.setDuration(5);
                player.addEffect(effect);
                room.effectCD = 10;
            }
        }else if (room.getPlayerMode(player) == 3) {
            player.removeAllEffects();
        }
    }

    /**
     * 玩家点击事件
     * @param event 事件
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player == null || block == null) {
            return;
        }
        Level level = player.getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
            return;
        }
        Server.getInstance().getScheduler().scheduleAsyncTask(MurderMystery.getInstance(), new AsyncTask() {
            @Override
            public void onRun() {
                if (block.getId() == 118 &&
                        block.getLevel().getBlock(block.getFloorX(), block.getFloorY() - 1, block.getFloorZ()).getId() == 138) {
                    int x = 0; //金锭数量
                    for (Item item : player.getInventory().getContents().values()) {
                        if (item.getId() == 266) {
                            x += item.getCount();
                        }
                    }
                    if (x > 0) {
                        player.getInventory().removeItem(Item.get(266, 0, 1));
                        Item item = Item.get(373, 0, 1);
                        item.setCustomName("§a神秘药水");
                        item.setLore("未知效果的药水", "究竟是会带来好运，还是厄运？");
                        player.getInventory().addItem(item);
                        player.sendMessage("§a成功兑换到一瓶神秘药水！");
                    }else {
                        player.sendMessage("§a需要使用金锭兑换药水！");
                    }
                }
            }
        });
    }

    /**
     * 玩家使用消耗品事件
     * @param event 事件
     */
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (player == null || item == null) {
            return;
        }
        Level level = player.getLevel();
        if (level == null || !MurderMystery.getInstance().getRooms().containsKey(level.getName()) ||
                MurderMystery.getInstance().getRooms().get(level.getName()).getMode() != 2) {
            return;
        }
        if (item.getCustomName().equals("§a神秘药水")) {
            int random = new Random().nextInt(100);
            Effect effect;
            if (random < 100 && random >= 70) {
                effect = Effect.getEffect(1); //速度
            }else if (random < 70 && random >= 60) {
                effect = Effect.getEffect(16); //夜视
            }else if (random < 60 && random >= 50) {
                effect = Effect.getEffect(14); //隐身
            }else if (random < 50 && random >= 30) {
                effect = Effect.getEffect(8); //跳跃提升
                effect.setAmplifier(2);
            }else {
                effect = Effect.getEffect(2); //缓慢
            }
            effect.setDuration(100);
            player.addEffect(effect);
        }
    }

}
