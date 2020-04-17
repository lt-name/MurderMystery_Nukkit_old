package name.murdermystery.tasks.game;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import name.murdermystery.MurderMystery;
import name.murdermystery.room.Room;


/**
 * 信息显示
 */
public class TipsTask extends PluginTask<MurderMystery> {

    private Room room;

    public TipsTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            this.cancel();
        }
        if (this.room.getPlayers().values().size() > 0) {
            int playerNumber = 0;
            for (Integer integer : this.room.getPlayers().values()) {
                if (integer != 0) {
                    playerNumber++;
                }
            }
            this.sendActionBar("§a距游戏结束还有 "+ this.room.gameTime + " 秒\n当前还有： §e" + playerNumber + " §a人存活");
        }
    }

    private void sendActionBar(String string) {
        String mode;
        for (Player player : this.room.getPlayers().keySet()) {
            switch (this.room.getPlayerMode(player)) {
                case 1:
                    mode = "平民";
                    break;
                case 2:
                    mode = "侦探";
                    break;
                case 3:
                    mode = "杀手";
                    if (room.effectCD > 0 ) {
                        mode += " 加速冷却剩余：" + room.effectCD + "秒";
                    }
                    break;
                default:
                    mode = "死亡";
                    break;
            }
            player.sendActionBar("§a身份：" + mode + "\n" + string);
        }
    }

}
