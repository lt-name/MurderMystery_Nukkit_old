package cn.lanink.murdermystery.tasks;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;


public class VictoryTask extends PluginTask<MurderMystery> {

    private Room room;
    private int victoryTime, victory;

    public VictoryTask(MurderMystery owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victoryTime = 10;
        this.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 3) {
            this.cancel();
        }
        if (this.victoryTime < 1) {
            this.room.endGame();
            this.room.setMode(1);
            owner.getServer().getScheduler().scheduleRepeatingTask(
                    MurderMystery.getInstance(), new WaitTask(MurderMystery.getInstance(), this.room), 20,true);
            this.cancel();
        }else {
            this.victoryTime--;
            owner.getServer().getScheduler().scheduleAsyncTask(MurderMystery.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    for (Player player : room.getPlayers().keySet()) {
                        if (room.getPlayers().get(player) != 0) {
                            if (victory == 1 && room.getPlayers().get(player) == 3) {
                                continue;
                            }
                            Tools.spawnFirework(player);
                        }
                    }
                    if (MurderMystery.getInstance().getConfig().getBoolean("底部显示信息", true)) {
                        if (victory == 3) {
                            this.sendActionBar("§e恭喜杀手获得胜利！");
                        } else {
                            this.sendActionBar("§e恭喜平民和侦探获得胜利！");
                        }
                    }
                }

                private void sendActionBar(String string) {
                    for (Player player : room.getPlayers().keySet()) {
                        player.sendActionBar(string);
                    }
                }
            });
        }
    }



}
