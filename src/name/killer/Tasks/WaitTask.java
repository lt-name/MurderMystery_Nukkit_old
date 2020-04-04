package name.killer.Tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.Room;

import java.util.LinkedHashMap;
import java.util.Random;

public class WaitTask extends PluginTask<Killer> {

    private Room gameRoom;

    public WaitTask(Killer owner, Room gameRoom) {
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
                LinkedHashMap<Player, Integer> players = this.gameRoom.getPlayers();
                int random1 = new Random().nextInt(players.size()) + 1;
                int random2;
                do {
                    random2 = new Random().nextInt(players.size()) + 1;
                }while (random1 == random2);
                int j = 0;
                for (Player player : players.keySet()) {
                    j++;
                    if (j == random1) {
                        this.gameRoom.addPlaying(player, 2);
                        continue;
                    }
                    if (j == random2) {
                        this.gameRoom.addPlaying(player, 3);
                        continue;
                    }
                    this.gameRoom.addPlaying(player, 1);
                }
                this.gameRoom.setMode(2);
                owner.getServer().getScheduler().scheduleRepeatingTask(
                        Killer.getInstance(), new GameTask(Killer.getInstance(), this.gameRoom), 20,true);
                this.cancel();
            }
        }else {
            if (this.gameRoom.waitTime != this.gameRoom.getWaitTime()) {
                this.gameRoom.waitTime = this.gameRoom.getWaitTime();
            }
            this.sendActionBar("§c等待玩家加入中,当前已有" + this.gameRoom.getPlayers().size() + "位玩家");
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.sendTip(string);
        }
    }

}
