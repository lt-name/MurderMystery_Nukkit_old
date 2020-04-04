package name.killer.Tasks;

import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.Room;
import name.killer.Utils.LevelFileReset;


public class ResetLevelTask extends PluginTask<Killer> {

    private Room gameRoom;

    public ResetLevelTask(Killer owner, Room gameRoom) {
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
