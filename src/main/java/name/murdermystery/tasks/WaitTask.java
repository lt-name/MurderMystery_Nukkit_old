package name.murdermystery.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;
import name.murdermystery.MurderMystery;
import name.murdermystery.event.MurderRoomStartEvent;
import name.murdermystery.room.Room;
import name.murdermystery.utils.Tools;

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
                Server.getInstance().getPluginManager().callEvent(new MurderRoomStartEvent(this.room));
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
