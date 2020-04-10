package main.java.name.murdermystery.tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.utils.Tools;


public class VictoryTask extends PluginTask<MurderMystery> {

    private Room room;
    private int victory;

    public VictoryTask(MurderMystery owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 3) {
            this.cancel();
        }
        if (this.room.victoryTime < 1) {
            this.room.endGame();
            this.room.setMode(1);
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    MurderMystery.getInstance(), new WaitTask(MurderMystery.getInstance(), this.room), 20,true);
            this.cancel();
        }else {
            this.room.victoryTime--;
            for (Player player : this.room.getPlayers().keySet()) {
                if (this.room.getPlayers().get(player) != 0) {
                    if (victory == 1 && this.room.getPlayers().get(player) == 3) {
                       continue;
                    }
                    Tools.spawnFirework(player);
                }
            }
            if (owner.getActionBar()) {
                if (victory == 3) {
                    this.sendActionBar("§e恭喜杀手获得胜利！");
                }else {
                    this.sendActionBar("§e恭喜平民和侦探获得胜利！");
                }
            }
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

}
