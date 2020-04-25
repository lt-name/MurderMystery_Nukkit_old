package cn.lanink.murdermystery.listener;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.entity.EntityPlayerCorpse;
import cn.lanink.murdermystery.event.*;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.tasks.game.GoldTask;
import cn.lanink.murdermystery.tasks.game.TimeTask;
import cn.lanink.murdermystery.tasks.game.TipsTask;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import me.onebone.economyapi.EconomyAPI;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * 游戏监听器（插件事件）
 * @author lt_name
 */
public class MurderListener implements Listener {

    /**
     * 房间开始事件
     * @param event 事件
     */
    @EventHandler
    public void onRoomStart(MurderRoomStartEvent event) {
        Room room = event.getRoom();
        Server.getInstance().getPluginManager().callEvent(new MurderRoomChooseIdentityEvent(room));
        if (room.getRandomSpawn().size() > 0) {
            int x=0;
            for (Player player : room.getPlayers().keySet()) {
                if (room.getRandomSpawn().get(x) != null) {
                    String[] s = room.getRandomSpawn().get(x).split(":");
                    player.teleport(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
                }else {
                    x=0;
                    player.teleport(room.getSpawn());
                }
                x++;
            }
        }
        room.setMode(2);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                MurderMystery.getInstance(), new TimeTask(MurderMystery.getInstance(), room), 20,true);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                MurderMystery.getInstance(), new GoldTask(MurderMystery.getInstance(), room), 20, true);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                MurderMystery.getInstance(), new TipsTask(MurderMystery.getInstance(), room), 20);
    }

    /**
     * 玩家分配身份事件
     * @param event 事件
     */
    @EventHandler
    public void onChooseIdentity(MurderRoomChooseIdentityEvent event) {
        if (!event.isCancelled()) {
            Room room = event.getRoom();
            LinkedHashMap<Player, Integer> players = room.getPlayers();
            int random1 = new Random().nextInt(players.size()) + 1;
            int random2;
            do {
                random2 = new Random().nextInt(players.size()) + 1;
            }while (random1 == random2);
            int j = 0;
            for (Player player : players.keySet()) {
                j++;
                player.getInventory().clearAll();
                //侦探
                if (j == random1) {
                    room.addPlaying(player, 2);
                    player.sendTitle("§e侦探", "找出杀手，并用弓箭击杀他", 10, 40, 10);
                    continue;
                }
                //杀手
                if (j == random2) {
                    room.addPlaying(player, 3);
                    player.sendTitle("§c杀手", "杀掉所有人", 10, 40, 10);
                    continue;
                }
                room.addPlaying(player, 1);
                player.sendTitle("§a平民", "活下去，就是胜利", 10, 40, 10);
            }
        }
    }

    /**
     * 玩家被攻击事件(符合游戏条件的攻击)
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDamage(MurderPlayerDamageEvent event) {
        if (!event.isCancelled()) {
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
                    player1.sendMessage("§a你成功击杀了杀手！");
                    int money = MurderMystery.getInstance().getConfig().getInt("击杀杀手额外奖励", 0);
                    if (money > 0) {
                        EconomyAPI.getInstance().addMoney(player1, money);
                        player1.sendMessage("§a你获得了额外奖励: " + money + " 元！");
                    }
                    player2.sendTitle("§c死亡", "§c你被击杀了", 10, 20, 20);
                } else {
                    player1.sendTitle("§c死亡", "§c你击中了队友", 20, 60, 20);
                    player2.sendTitle("§c死亡", "§c你被队友打死了", 20, 60, 20);
                    Server.getInstance().getPluginManager().callEvent(new MurderPlayerDeathEvent(room, player1));
                }
            }
            Server.getInstance().getPluginManager().callEvent(new MurderPlayerDeathEvent(room, player2));
        }
    }

    /**
     * 玩家死亡事件（游戏中死亡）
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDeath(MurderPlayerDeathEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            Room room = event.getRoom();
            if (player == null || room == null) {
                return;
            }
            player.getInventory().clearAll();
            player.setAllowModifyWorld(true);
            player.setAdventureSettings((new AdventureSettings(player)).set(AdventureSettings.Type.ALLOW_FLIGHT, true));
            player.setGamemode(3);
            if (room.getPlayerMode(player) == 2) {
                room.getLevel().dropItem(player, Tools.getMurderItem(1));
            }
            room.addPlaying(player, 0);
            Tools.setPlayerInvisible(player, true);
            Tools.addSound(room, Sound.GAME_PLAYER_HURT);
            Server.getInstance().getPluginManager().callEvent(new MurderPlayerCorpseSpawnEvent(room, player));
        }
    }

    /**
     * 尸体生成事件
     * @param event 事件
     */
    @EventHandler
    public void onCorpseSpawn(MurderPlayerCorpseSpawnEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            Room room = event.getRoom();
            if (player == null || room == null) {
                return;
            }
            CompoundTag nbt = EntityPlayerCorpse.getDefaultNBT(player);
            nbt.putCompound("Skin", new CompoundTag()
                    .putByteArray("Data", room.getPlayerSkin(player).getSkinData().data)
                    .putString("ModelId", room.getPlayerSkin(player).getSkinId()));
            nbt.putFloat("Scale", -1.0F);
            EntityPlayerCorpse ent = new EntityPlayerCorpse(player.getChunk(), nbt);
            ent.setSkin(room.getPlayerSkin(player));
            ent.setPosition(new Vector3(player.getFloorX(), Tools.getFloorY(player), player.getFloorZ()));
            ent.setGliding(true);
            ent.setRotation(player.getYaw(), 0);
            ent.spawnToAll();
            ent.updateMovement();
        }
    }

}
