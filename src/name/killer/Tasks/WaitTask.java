package name.killer.Tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.GameRoom;

public class WaitTask extends PluginTask<Killer> {

    private GameRoom gameRoom;

    public WaitTask(Killer owner, GameRoom gameRoom) {
        super(owner);
        this.gameRoom = gameRoom;
    }

    @Override
    public void onRun(int i) {
        if (this.gameRoom.getPlayers().size() >= 5) {
            if (this.gameRoom.waitTime > 0) {
                this.gameRoom.waitTime--;
                this.sendActionBar("§a当前已有" + this.gameRoom.getPlayers().size() + "位玩家" +
                        "\n§a游戏还有" + this.gameRoom.waitTime + "秒开始！");
            }else {
                this.gameRoom.startGame();
                owner.getServer().getScheduler().scheduleRepeatingTask(
                        Killer.getInstance(), new GameTask(Killer.getInstance(), this.gameRoom), 20,true);
                this.sendActionBar("§e游戏开始！");
                this.cancel();
            }
        }else {
            if (this.gameRoom.waitTime != 120) { this.gameRoom.waitTime = 120; }
            this.sendActionBar("§c等待玩家加入中,当前已有" + this.gameRoom.getPlayers().size() + "位玩家");
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

}
