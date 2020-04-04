package name.killer.Tasks;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.Room;

import java.util.List;

public class GameTask extends PluginTask<Killer> {

    private Room gameRoom;

    public GameTask(Killer owner, Room gameRoom) {
        super(owner);
        this.gameRoom = gameRoom;
    }

    public void onRun(int i) {
        if (this.gameRoom.getMode() == 2) {
            //游戏时间
            if (this.gameRoom.gameTime > 0) {
                this.gameRoom.gameTime--;
                this.sendActionBar("距游戏结束还有"+ this.gameRoom.gameTime + "秒");
            }else {
                this.gameRoom.endGame(true);
            }
            //金锭生成
            if (this.gameRoom.goldSpawnTime < 1) {
                List<String> goldSpawn = this.gameRoom.getGoldSpawn();
                for (String spawn : goldSpawn) {
                    String[] s = spawn.split(":");
                    this.gameRoom.getWorld().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                            Item.get(266, 0));
                }
                this.gameRoom.goldSpawnTime = this.gameRoom.getGoldSpawnTime();
            }else {
                this.gameRoom.goldSpawnTime--;
            }
        }else {
            this.cancel();
        }
    }

    private void sendActionBar(String string) {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

}
