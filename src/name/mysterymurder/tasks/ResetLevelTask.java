package name.mysterymurder.tasks;

import cn.nukkit.scheduler.PluginTask;
import name.mysterymurder.MysteryMurder;
import name.mysterymurder.room.Room;
import name.mysterymurder.utils.LevelFileReset;


public class ResetLevelTask extends PluginTask<MysteryMurder> {

    private Room gameRoom;

    public ResetLevelTask(MysteryMurder owner, Room gameRoom) {
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
