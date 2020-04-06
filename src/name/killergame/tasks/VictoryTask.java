package name.killergame.tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import name.killergame.KillerGame;
import name.killergame.room.Room;
import name.killergame.utils.Tools;

public class VictoryTask extends PluginTask<KillerGame> {

    private Room room;
    private int victory;

    public VictoryTask(KillerGame owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.victoryTime < 1) {
            this.room.endGame();
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    KillerGame.getInstance(), new WaitTask(KillerGame.getInstance(), this.room), 20,true);
            this.cancel();
        }else {
            this.room.victoryTime--;
            for (Player player : this.room.getPlayers().keySet()) {
                if (this.room.getPlayers().get(player) != 0) {
                    Tools.spawnFirework(player);
                }
            }
            if (victory == 3) {
                this.sendActionBar("§e恭喜杀手获得胜利！");
            }else {
                this.sendActionBar("§e恭喜侦探和平民获得胜利！");
            }
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.room.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

}
