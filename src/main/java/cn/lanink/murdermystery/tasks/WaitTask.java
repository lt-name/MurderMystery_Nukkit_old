package cn.lanink.murdermystery.tasks;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.event.MurderRoomStartEvent;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;

public class WaitTask extends PluginTask<MurderMystery> {

    private final Room room;

    public WaitTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 1) {
            this.cancel();
        }
        if (this.room.getPlayers().size() >= 5) {
            if (this.room.waitTime > 0) {
                this.room.waitTime--;
                if (this.room.waitTime <= 5) {
                    Tools.addSound(this.room, Sound.RANDOM_CLICK);
                }
            }else {
                owner.getServer().getPluginManager().callEvent(new MurderRoomStartEvent(this.room));
                this.cancel();
            }
        }else if (this.room.getPlayers().size() > 0) {
            if (this.room.waitTime != this.room.getWaitTime()) {
                this.room.waitTime = this.room.getWaitTime();
            }
        }else {
            this.room.endGame();
            this.cancel();
        }
    }

}
