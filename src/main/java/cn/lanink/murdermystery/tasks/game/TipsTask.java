package cn.lanink.murdermystery.tasks.game;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.Room;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;
import tip.messages.ScoreBoardMessage;
import tip.utils.Api;

import java.util.LinkedList;


/**
 * 信息显示
 */
public class TipsTask extends PluginTask<MurderMystery> {

    private final Room room;
    private final boolean scoreBoard;
    private ScoreBoardMessage scoreBoardMessage;

    public TipsTask(MurderMystery owner, Room room) {
        super(owner);
        this.room = room;
        this.scoreBoard = MurderMystery.getInstance().getConfig().getBoolean("计分板显示信息", false);
        if (this.scoreBoard) {
            this.scoreBoardMessage = new ScoreBoardMessage(
                    room.getLevel().getName(), true, "§e密室杀人", new LinkedList<>());

        }
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            if (room.getPlayers().values().size() > 0) {
                room.getPlayers().keySet().forEach(
                        player -> Api.removePlayerShowMessage(player.getName(), this.scoreBoardMessage));
            }
            this.cancel();
        }
        Server.getInstance().getScheduler().scheduleAsyncTask(MurderMystery.getInstance(), new AsyncTask() {
            @Override
            public void onRun() {
                if (room.getPlayers().values().size() > 0) {
                    int playerNumber = 0;
                    for (Integer integer : room.getPlayers().values()) {
                        if (integer != 0) {
                            playerNumber++;
                        }
                    }
                    String mode;
                    for (Player player : room.getPlayers().keySet()) {
                        switch (room.getPlayerMode(player)) {
                            case 1:
                                mode = "平民";
                                break;
                            case 2:
                                mode = "侦探";
                                break;
                            case 3:
                                mode = "杀手";
                                break;
                            default:
                                mode = "死亡";
                                break;
                        }
                        if (scoreBoard) {
                            LinkedList<String> ms = new LinkedList<>();
                            ms.add("§a当前身份： §l§e" + mode + " ");
                            ms.add("§a剩余时间： §l§e" + room.gameTime + " ");
                            ms.add("§a剩余时间： §l§e" + room.gameTime + " ");
                            ms.add("§a存活人数： §l§e" + playerNumber + " ");
                            scoreBoardMessage.setMessages(ms);
                            Api.setPlayerShowMessage(player.getName(), scoreBoardMessage);
                        }
                        if (MurderMystery.getInstance().getConfig().getBoolean("底部显示信息", true)) {
                            if (room.getPlayerMode(player) == 3 && room.effectCD > 0) {
                                mode += " 加速冷却剩余：" + room.effectCD + "秒";
                            }
                            player.sendActionBar("§a身份：" + mode + "\n§a距游戏结束还有 "+ room.gameTime + " 秒\n当前还有： §e" + playerNumber + " §a人存活");
                        }
                    }
                }
            }
        });
    }

}
