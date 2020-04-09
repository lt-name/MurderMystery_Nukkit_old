package main.java.name.murdermystery.tasks;

import cn.nukkit.scheduler.PluginTask;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.utils.LevelFileReset;


public class ResetLevelTask extends PluginTask<MurderMystery> {

    private Room gameRoom;

    public ResetLevelTask(MurderMystery owner, Room gameRoom) {
        super(owner);
        this.gameRoom = gameRoom;
    }

    @Override
    public void onRun(int i) {
        LevelFileReset levelFileReset = new LevelFileReset();
        if (levelFileReset.resetLevel(this.gameRoom.getWorld())) {
            gameRoom.setMode(1);
            this.cancel();
        }
    }
}
