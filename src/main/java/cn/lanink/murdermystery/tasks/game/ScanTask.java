package cn.lanink.murdermystery.tasks.game;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.entity.EntityText;
import cn.lanink.murdermystery.room.Room;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;

import java.util.ArrayList;
import java.util.Map;

public class ScanTask extends AsyncTask {

    private final Room room;
    private final Player player;

    public ScanTask(Room room , Player player) {
        this.room = room;
        this.player = player;
    }

    @Override
    public void onRun() {
        ArrayList<Player> players = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : this.room.getPlayers().entrySet()) {
            if (entry.getValue() == 1 || entry.getValue() == 2) {
                players.add(entry.getKey());
            }
        }
        for (Player p : players) {
            EntityText text = new EntityText(p.getChunk(), EntityText.getDefaultNBT(p), p);
            text.spawnTo(player);
            Server.getInstance().getScheduler().scheduleDelayedTask(MurderMystery.getInstance(), new Task() {
                @Override
                public void onRun(int i) {
                    text.close();
                }
            }, 100);
        }
        this.player.sendMessage("§a已显示所有玩家位置！");
    }

}
