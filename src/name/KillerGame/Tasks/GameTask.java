package name.KillerGame.Tasks;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;
import name.KillerGame.KillerGame;
import name.KillerGame.Room.Room;

import java.util.List;
import java.util.Map;

public class GameTask extends PluginTask<KillerGame> {

    private Room gameRoom;

    public GameTask(KillerGame owner, Room gameRoom) {
        super(owner);
        this.gameRoom = gameRoom;
    }

    public void onRun(int i) {
        //游戏时间
        if (this.gameRoom.gameTime >= this.gameRoom.getGameTime()-10) {
            this.gameRoom.gameTime--;
            int time = this.gameRoom.gameTime - (this.gameRoom.getGameTime() - 10);
            this.sendMessage("杀手将在" + time + "秒后拿到剑！");
            if (time <= 5 && time > 1) {
                addSound();
            }else if (time < 1){
                for (Map.Entry<Player, Integer> entry : this.gameRoom.getPlayers().entrySet()) {
                    if (entry.getValue() == 1) {
                        entry.getKey().getInventory().addItem(Item.get(261, 0, 1));
                        entry.getKey().getInventory().addItem(Item.get(262, 0, 1));
                    }else if (entry.getValue() == 2) {
                        entry.getKey().getInventory().addItem(Item.get(267, 0, 1));
                    }
                }
            }
        }else if (this.gameRoom.gameTime > 0) {
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
            for (Entity entity : this.gameRoom.getWorld().getEntities()) {
                if (entity instanceof EntityItem) {
                    entity.close();
                }
            }
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

    private void sendMessage(String string) {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.sendMessage(string);
        }
    }

    private void addSound() {
        for (Player player : this.gameRoom.getPlayers().keySet()) {
            player.getLevel().addSound(new Vector3(player.x, player.y, player.z), Sound.RANDOM_CLICK);
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
                KillerGame.getInstance(), new VictoryTask(KillerGame.getInstance(), this.gameRoom, i), 20,true);
        this.cancel();
    }

}
