package cn.lanink.murdermystery.tasks;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.Room;
import cn.lanink.murdermystery.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;
import tip.messages.ScoreBoardMessage;
import tip.messages.TipMessage;
import tip.utils.Api;

import java.util.Arrays;
import java.util.LinkedList;


/**
 * 信息显示
 */
public class TipsTask extends PluginTask<MurderMystery> {

    private final String taskName = "TipsTask";
    private final Room room;
    private final boolean bottom, scoreBoard;
    private TipMessage bottomMessage;
    private ScoreBoardMessage scoreBoardMessage;
    private final Language language;

    public TipsTask(MurderMystery owner, Room room) {
        super(owner);
        this.language = owner.getLanguage();
        this.room = room;
        this.bottom = owner.getConfig().getBoolean("底部显示信息", true);
        this.scoreBoard = owner.getConfig().getBoolean("计分板显示信息", false);
        this.bottomMessage = new TipMessage(room.getLevel().getName(), true, 0, null);
        this.scoreBoardMessage = new ScoreBoardMessage(
                room.getLevel().getName(), true, this.language.scoreBoardTitle, new LinkedList<>());
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() == 0) {
            if (this.room.getPlayers().values().size() > 0) {
                this.room.getPlayers().keySet().forEach(player -> {
                            Api.removePlayerShowMessage(player.getName(), this.scoreBoardMessage);
                            Api.removePlayerShowMessage(player.getName(), this.bottomMessage);
                        });
            }
            this.cancel();
        }
        if (!this.room.task.contains(this.taskName)) {
            this.room.task.add(this.taskName);
            owner.getServer().getScheduler().scheduleAsyncTask(MurderMystery.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    if (room.getPlayers().values().size() > 0) {
                        if (room.getMode() == 1) {
                            if (room.getPlayers().values().size() > 5) {
                                bottomMessage.setMessage(language.waitTimeBottom
                                        .replace("%playerNumber%", room.getPlayers().size() + "")
                                        .replace("%time%", room.waitTime + ""));
                                LinkedList<String> ms = new LinkedList<>();
                                for (String string : language.waitTimeScoreBoard.split("\n")) {
                                    ms.add(string.replace("%playerNumber%", room.getPlayers().size() + "")
                                            .replace("%time%", room.waitTime + ""));
                                }
                                scoreBoardMessage.setMessages(ms);
                            }else {
                                bottomMessage.setMessage(language.waitBottom
                                        .replace("%playerNumber%", room.getPlayers().size() + ""));
                                LinkedList<String> ms = new LinkedList<>();
                                for (String string : language.waitScoreBoard.split("\n")) {
                                    ms.add(string.replace("%playerNumber%", room.getPlayers().size() + ""));
                                }
                                scoreBoardMessage.setMessages(ms);
                            }
                            this.sendMessage();
                        }else if (room.getMode() == 2) {
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
                                        mode = owner.getLanguage().commonPeople;
                                        break;
                                    case 2:
                                        mode = owner.getLanguage().detective;
                                        break;
                                    case 3:
                                        mode = owner.getLanguage().killer;
                                        break;
                                    default:
                                        mode = owner.getLanguage().death;
                                        break;
                                }
                                if (bottom) {
                                    bottomMessage.setMessage(language.gameTimeBottom.replace("%mode%", mode)
                                            .replace("%playerNumber%", playerNumber + "")
                                            .replace("%time%", room.gameTime + ""));
                                    Api.setPlayerShowMessage(player.getName(), bottomMessage);
                                }
                                if (scoreBoard) {
                                    LinkedList<String> ms = new LinkedList<>();
                                    for (String string : language.gameTimeScoreBoard.split("\n")) {
                                        ms.add(string.replace("%mode%", mode)
                                                .replace("%playerNumber%", playerNumber + "")
                                                .replace("%time%", room.gameTime + ""));
                                    }
                                    if (room.getPlayerMode(player) == 3) {
                                        if (room.effectCD > 0) {
                                            ms.add(language.gameEffectCDScoreBoard
                                                    .replace("%time%", room.effectCD + ""));
                                        }
                                        if (room.swordCD > 0) {
                                            ms.add(language.gameSwordCDScoreBoard
                                                    .replace("%time%", room.swordCD + ""));
                                        }
                                        if (room.scanCD > 0) {
                                            ms.add(language.gameScanCDScoreBoard
                                                    .replace("%time%", room.scanCD + ""));
                                        }
                                    }
                                    scoreBoardMessage.setMessages(ms);
                                    Api.setPlayerShowMessage(player.getName(), scoreBoardMessage);
                                }
                            }
                        }else if (room.getMode() == 3) {
                            if (room.victory == 3) {
                                bottomMessage.setMessage(language.victoryKillerBottom);
                                LinkedList<String> ms = new LinkedList<>(Arrays.asList(language.victoryKillerScoreBoard.split("\n")));
                                scoreBoardMessage.setMessages(ms);
                            } else {
                                bottomMessage.setMessage(language.victoryCommonPeopleBottom);
                                LinkedList<String> ms = new LinkedList<>(Arrays.asList(language.victoryCommonPeopleScoreBoard.split("\n")));
                                scoreBoardMessage.setMessages(ms);
                            }
                            this.sendMessage();
                        }
                        room.task.remove(taskName);
                    }
                }

                private void sendMessage() {
                    for (Player player : room.getPlayers().keySet()) {
                        if (bottom) {
                            Api.setPlayerShowMessage(player.getName(), bottomMessage);
                        }
                        if (scoreBoard) {
                            Api.setPlayerShowMessage(player.getName(), scoreBoardMessage);
                        }
                    }
                }

            });
        }
    }

}
