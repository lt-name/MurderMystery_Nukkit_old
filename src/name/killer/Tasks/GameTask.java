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
        //游戏时间
        if (this.gameRoom.gameTime > 0) {
            this.gameRoom.gameTime--;
            int j = 0;
            boolean killer = false;
            for (Integer integer : this.gameRoom.getPlayers().values()) {
                if (integer != 0) {
                    j++;
                }
                if (integer == 3) {
                    killer = true;
                }
            }
            this.sendActionBar("距游戏结束还有"+ this.gameRoom.gameTime + "秒+\n当前还有：" + j + "人存活");
            if (killer) {
                if (j < 2) {
                    victory(3);
                }
            }else {
                victory(1);
            }
        }else {
            victory(1);
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
    }

    private void sendActionBar(String string) {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.sendActionBar(string);
        }
    }

    private void victory(int i) {
        this.gameRoom.setMode(3);
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            if (i == 3) {
                player.sendTitle("杀手获得胜利！", "", 10, 30, 10);
            }else {
                player.sendTitle("平民和侦探获得胜利！", "", 10, 30, 10);
            }
        }
        owner.getServer().getScheduler().scheduleRepeatingTask(
                Killer.getInstance(), new VictoryTask(Killer.getInstance(), this.gameRoom, i), 20,true);
        this.cancel();
    }

}
