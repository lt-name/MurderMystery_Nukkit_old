package main.java.name.murdermystery.tasks.game;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.tasks.VictoryTask;
import main.java.name.murdermystery.tasks.WaitTask;
import main.java.name.murdermystery.utils.Tools;

import java.util.Map;

/**
 * 游戏时间计算
 */
public class TimeTask extends PluginTask<MurderMystery> {

    private Room room;

    public TimeTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
    }

    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            this.cancel();
        }
        //计时与胜利判断
        if (this.room.gameTime > 0) {
            this.room.gameTime--;
            int playerNumber = 0;
            boolean killer = false;
            for (Integer integer : this.room.getPlayers().values()) {
                if (integer != 0) {
                    playerNumber++;
                }
                if (integer == 3) {
                    killer = true;
                }
            }
            if (killer) {
                if (playerNumber < 2) {
                    victory(3);
                }
            }else {
                victory(1);
            }
        }else {
            victory(1);
        }
        //开局10秒后给物品
        if (this.room.gameTime >= this.room.getGameTime()-10) {
            int time = this.room.gameTime - (this.room.getGameTime() - 10);
            if (time <= 5 && time >= 1) {
                this.sendMessage("§e杀手将在" + time + "秒后拿到剑！");
                Tools.addSound(this.room, Sound.RANDOM_CLICK);
            }else if (time < 1) {
                this.sendMessage("§e杀手已拿到剑！");
                for (Map.Entry<Player, Integer> entry : this.room.getPlayers().entrySet()) {
                    if (entry.getValue() == 2) {
                        Item item = Item.get(261, 0, 1);
                        item.setCustomName("§e侦探之弓");
                        entry.getKey().getInventory().setItem(1, item);
                        entry.getKey().getInventory().setItem(2, Item.get(262, 0, 1));
                    }else if (entry.getValue() == 3) {
                        Item item = Item.get(267, 0, 1);
                        item.setCustomName("§c杀手之剑");
                        entry.getKey().getInventory().setItem(1, item);
                    }
                }
            }
        }
        //杀手加速cd计算
        if (room.effectCD > 0) {
            room.effectCD--;
        }
    }

    private void sendMessage(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendMessage(string);
        }
    }

    private void victory(int victoryMode) {
        if (this.room.getPlayers().values().size() > 0) {
            this.room.setMode(3);
            for (Player player : this.room.getPlayers().keySet()) {
                if (victoryMode == 3) {
                    player.sendTitle("§a杀手获得胜利！", "", 10, 30, 10);
                }else {
                    player.sendTitle("§a平民和侦探获得胜利！", "", 10, 30, 10);
                }
            }
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    MurderMystery.getInstance(), new VictoryTask(MurderMystery.getInstance(), this.room, victoryMode), 20,true);
        }else {
            this.room.setMode(1);
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    MurderMystery.getInstance(), new WaitTask(MurderMystery.getInstance(), this.room), 20,true);
        }
        this.cancel();
    }

}
