package name.killer.Tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.Room;
import name.killer.Utils.Tools;

public class VictoryTask extends PluginTask<Killer> {

    private Room room;
    private int victory;

    public VictoryTask(Killer owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.victoryTime < 1) {
            this.room.endGame();
        }else {
            this.room.victoryTime--;
            for (Player player : this.room.getPlayers().keySet()) {
                if (this.room.getPlayers().get(player) != 0) {
                    Tools.spawnFirework(player);
                }
            }
            if (victory == 3) {
                sendActionBar("§e恭喜杀手获得胜利！");
            }else {
                sendActionBar("§e恭喜侦探和平民获得胜利！");
            }
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

}
