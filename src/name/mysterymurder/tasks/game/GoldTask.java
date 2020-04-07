package name.mysterymurder.tasks.game;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;
import name.mysterymurder.MysteryMurder;
import name.mysterymurder.room.Room;
import name.mysterymurder.utils.Tools;

/**
 * 金锭生成 金锭自动兑换
 */
public class GoldTask extends PluginTask<MysteryMurder> {

    private Room room;

    public GoldTask(MysteryMurder owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            this.cancel();
        }
        //金锭生成
        if (this.room.goldSpawnTime < 1) {
            Tools.cleanEntity(this.room.getWorld());
            for (String spawn : this.room.getGoldSpawn()) {
                String[] s = spawn.split(":");
                this.room.getWorld().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(266, 0));
            }
            this.room.goldSpawnTime = this.room.getGoldSpawnTime();
        }else {
            this.room.goldSpawnTime--;
        }
        //判断玩家金锭数量
        for (Player player : this.room.getPlayers().keySet()) {
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

}
