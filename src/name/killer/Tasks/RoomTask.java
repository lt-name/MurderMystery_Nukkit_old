package name.killer.Tasks;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;
import name.killer.Room.GameRoom;

import java.util.ArrayList;
import java.util.List;

public class RoomTask extends PluginTask<Killer> {

    private GameRoom gameRoom;

    public RoomTask(Killer owner, GameRoom gameRoom) {
        super(owner);
        this.gameRoom = gameRoom;
    }

    public void onRun(int i) {
        if (this.gameRoom.getMode() == 1 && this.gameRoom.goldSpawnTime == 0) {
            List<String> goldSpawn = this.gameRoom.getGoldSpawn();
            for (String spawn : goldSpawn) {
                String[] s = spawn.split(":");
                this.gameRoom.getWorld().dropItem(new Vector3(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])),
                        Item.get(266,0));
            }
            this.gameRoom.goldSpawnTime = this.gameRoom.getGoldSpawnTime();
        }else if (this.gameRoom.getMode() == 1){
            this.gameRoom.goldSpawnTime--;
            //防溢出
            if (this.gameRoom.goldSpawnTime<0){ this.gameRoom.goldSpawnTime=0; }
        }
    }

}
