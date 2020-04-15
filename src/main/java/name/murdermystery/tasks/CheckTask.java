package main.java.name.murdermystery.tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.room.Room;

import java.util.Map;

public class CheckTask extends PluginTask<MurderMystery> {

    private Room room;

    public CheckTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 1 || this.room.getMode() != 2) {
            this.cancel();
        }
        for (Map.Entry<Player, Integer> entry : this.room.getPlayers().entrySet()) {
            entry.getKey().setNameTag(" ");
            entry.getKey().setAllowModifyWorld(false);
        }
    }

}
