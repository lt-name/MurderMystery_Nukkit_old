package name.killergame.tasks.game;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;
import name.killergame.KillerGame;
import name.killergame.room.Room;
import name.killergame.tasks.VictoryTask;
import name.killergame.tasks.WaitTask;
import name.killergame.utils.Tools;

import java.util.Map;

/**
 * 游戏时间计算
 */
public class TimeTask extends PluginTask<KillerGame> {

    private Room room;

    public TimeTask(KillerGame owner, Room gameRoom) {
        super(owner);
        this.room = gameRoom;
    }

    public void onRun(int i) {
        //开局10秒后给物品
        if (this.room.gameTime >= this.room.getGameTime()-10) {
            int time = this.room.gameTime - (this.room.getGameTime() - 10);
            if (time <= 5 && time >= 1) {
                this.sendMessage("§e杀手将在" + time + "秒后拿到剑！");
                Tools.addSound(this.room, Sound.RANDOM_CLICK);
            }else if (time < 1){
                this.sendMessage("§e杀手已拿到剑！");
                for (Map.Entry<Player, Integer> entry : this.room.getPlayers().entrySet()) {
                    if (entry.getValue() == 2) {
                        entry.getKey().getInventory().setItem(1, Item.get(261, 0, 1));
                        entry.getKey().getInventory().setItem(2, Item.get(262, 0, 1));
                    }else if (entry.getValue() == 3) {
                        entry.getKey().getInventory().setItem(1, Item.get(267, 0, 1));
                    }
                }
            }
        }
        //计时与胜利判断
        if (this.room.gameTime > 0) {
            this.room.gameTime--;
            int j = 0;
            boolean killer = false;
            for (Integer integer : this.room.getPlayers().values()) {
                if (integer != 0) {
                    j++;
                }
                if (integer == 3) {
                    killer = true;
                }
            }
            if (killer) {
                if (j < 2) {
                    victory(3);
                }
            }else {
                victory(1);
            }
        }else {
            victory(1);
        }
    }

    private void sendMessage(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendMessage(string);
        }
    }

    private void victory(int i) {
        Tools.cleanEntity(this.room.getWorld());
        this.room.setMode(3);
        if (this.room.getPlayers().values().size() > 0) {
            for (Player player : this.room.getPlayers().keySet()) {
                if (i == 3) {
                    player.sendTitle("§a杀手获得胜利！", "", 10, 30, 10);
                }else {
                    player.sendTitle("§a平民和侦探获得胜利！", "", 10, 30, 10);
                }
            }
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    KillerGame.getInstance(), new VictoryTask(KillerGame.getInstance(), this.room, i), 20,true);
        }else {
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    KillerGame.getInstance(), new WaitTask(KillerGame.getInstance(), this.room), 20,true);
        }
        this.cancel();
    }

}
