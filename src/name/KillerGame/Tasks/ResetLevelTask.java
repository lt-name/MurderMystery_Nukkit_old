package name.KillerGame.Tasks;

import cn.nukkit.scheduler.PluginTask;
import name.KillerGame.KillerGame;
import name.KillerGame.Room.Room;
import name.KillerGame.Utils.LevelFileReset;


public class ResetLevelTask extends PluginTask<KillerGame> {

    private Room gameRoom;

    public ResetLevelTask(KillerGame owner, Room gameRoom) {
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
