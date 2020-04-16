package main.java.name.murdermystery.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.event.MurderPlayerDistributionEvent;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.tasks.game.GoldTask;
import main.java.name.murdermystery.tasks.game.TimeTask;
import main.java.name.murdermystery.tasks.game.TipsTask;
import main.java.name.murdermystery.utils.Tools;

public class WaitTask extends PluginTask<MurderMystery> {

    private Room room;

    public WaitTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getPlayers().size() >= 5) {
            if (this.room.getMode() != 1) {
                this.cancel();
            }
            if (this.room.waitTime > 0) {
                this.room.waitTime--;
                this.sendActionBar("§a当前已有: " + this.room.getPlayers().size() + " 位玩家" +
                        "\n§a游戏还有: " + this.room.waitTime + " 秒开始！");
                if (this.room.waitTime <= 5) {
                    Tools.addSound(this.room, Sound.RANDOM_CLICK);
                }
            }else {
                Server.getInstance().getPluginManager().callEvent(new MurderPlayerDistributionEvent(this.room));
                this.room.setMode(2);
                owner.getServer().getScheduler().scheduleRepeatingTask(
                        MurderMystery.getInstance(), new TimeTask(MurderMystery.getInstance(), this.room), 20,true);
                owner.getServer().getScheduler().scheduleRepeatingTask(
                        MurderMystery.getInstance(), new GoldTask(MurderMystery.getInstance(), this.room), 20, true);
                if (owner.getActionBar()) {
                    owner.getServer().getScheduler().scheduleRepeatingTask(
                            MurderMystery.getInstance(), new TipsTask(MurderMystery.getInstance(), this.room), 10, true);
                }
                this.cancel();
            }
        }else if (this.room.getPlayers().size() > 0) {
            if (this.room.waitTime != this.room.getWaitTime()) {
                this.room.waitTime = this.room.getWaitTime();
            }
            if (owner.getActionBar()) {
                this.sendActionBar("§c等待玩家加入中,当前已有: " + this.room.getPlayers().size() + " 位玩家");
            }
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendTip(string);
        }
    }

}
