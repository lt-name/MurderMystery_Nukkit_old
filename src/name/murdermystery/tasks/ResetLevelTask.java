package name.murdermystery.tasks;

import cn.nukkit.scheduler.PluginTask;
import name.murdermystery.MurderMystery;
import name.murdermystery.room.Room;
import name.murdermystery.utils.LevelFileReset;


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
