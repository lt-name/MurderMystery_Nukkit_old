package cn.lanink.murdermystery.tasks;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.util.Map;


public class VictoryTask extends PluginTask<MurderMystery> {

    private final Room room;
    private int victoryTime;

    public VictoryTask(MurderMystery owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victoryTime = 10;
        this.room.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 3) {
            this.cancel();
        }
        if (this.victoryTime < 1) {
            this.room.endGame();
            this.cancel();
        }else {
            this.victoryTime--;
            for (Map.Entry<Player, Integer> entry : room.getPlayers().entrySet()) {
                if (entry.getValue() != 0) {
                    if (room.victory == 1 && entry.getValue() == 3) {
                        continue;
                    }
                    Tools.spawnFirework(entry.getKey());
                }
            }
        }
    }



}
