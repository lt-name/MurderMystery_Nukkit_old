package cn.lanink.murdermystery.tasks.game;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;

/**
 * 金锭生成 金锭自动兑换
 */
public class GoldTask extends PluginTask<MurderMystery> {

    private final String taskName = "GoldTask";
    private final Room room;
    private int goldSpawnTime;

    public GoldTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
        this.goldSpawnTime = room.getGoldSpawnTime();
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            Tools.cleanEntity(this.room.getLevel());
            this.cancel();
        }
        if (this.goldSpawnTime < 1) {
            Tools.cleanEntity(this.room.getLevel());
            for (String spawn : this.room.getGoldSpawn()) {
                String[] s = spawn.split(":");
                this.room.getLevel().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(266, 0));
            }
            this.goldSpawnTime = this.room.getGoldSpawnTime();
        }else {
            this.goldSpawnTime--;
        }
        if (!this.room.task.contains(this.taskName)) {
            this.room.task.add(this.taskName);
            owner.getServer().getScheduler().scheduleAsyncTask(MurderMystery.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    if (room.getPlayers().values().size() > 0) {
                        for (Player player : room.getPlayers().keySet()) {
                            int x = 0;
                            boolean bow = true;
                            for (Item item : player.getInventory().getContents().values()) {
                                if (item.getId() == 266) {
                                    x += item.getCount();
                                    continue;
                                }
                                if (item.getId() == 261) {
                                    bow = false;
                                }
                            }
                            if (x > 9) {
                                player.getInventory().removeItem(Item.get(266, 0, 10));
                                player.getInventory().addItem(Item.get(262, 0, 1));
                                if (bow) {
                                    player.getInventory().addItem(Item.get(261, 0, 1));
                                }
                            }
                        }
                    }
                    room.task.remove(taskName);
                }
            });
        }
    }

}
