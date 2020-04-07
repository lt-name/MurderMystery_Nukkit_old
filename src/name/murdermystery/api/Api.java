package name.murdermystery.api;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import name.murdermystery.MurderMystery;
import name.murdermystery.room.Room;

import java.util.LinkedHashMap;

public class Api {



    /**
     * @return 房间列表
     */
    public static LinkedHashMap<String, Room> getRooms() {
        return MurderMystery.getInstance().getRooms();
    }

    /**
     * @param level 世界
     * @return 房间
     */
    public static Room getRoomByLevel(Level level) {
        return getRoomByLevel(level.getName());
    }

    /**
     * @param level 世界名称
     * @return 房间
     */
    public static Room getRoomByLevel(String level) {
        if (getRooms().containsKey(level)) {
            return getRooms().get(level);
        }
        return null;
    }

    /**
     * @deprecated
     * @param player 玩家
     * @return 玩家身份
     */
    public static String getPlayerMode(String player) {
        return getPlayerMode(MurderMystery.getInstance().getServer().getPlayer(player));
    }

    /**
     * @param player 玩家
     * @return 玩家身份
     */
    public static String getPlayerMode(Player player) {
        String mode = "当前身份: ";
        for (Room room : getRooms().values()) {
            if (room.getMode() == 2) {
                if (room.isPlaying(player)) {
                    switch (room.getPlayerMode(player)) {
                        case 1:
                            mode += "平民";
                        case 2:
                            mode += "侦探";
                        case 3:
                            mode += "杀手";
                        default:
                            mode += "死亡";
                    }
                    return mode;
                }
            }
        }
        mode +=  "无";
        return mode;
    }

    /**
     * 根据玩家获取倒计时
     * @param player 玩家
     * @return 剩余时间
     */
    public static String getTime(Player player) {
        for (Room room : getRooms().values()) {
            if (room.isPlaying(player)) {
                if (room.getMode() == 1) {
                    return "游戏还有： " + room.waitTime + "秒开始！";
                }else if (room.getMode() == 2) {
                    return "距游戏结束还有" + room.gameTime + "秒！";
                }
            }
        }
        return "游戏未开始";
    }

    /**
     * 获取存活玩家
     * @param player 玩家
     * @return 存活玩家数量
     */
    public static String getSurvivor(Player player) {
        for (Room room : getRooms().values()) {
            if (room.isPlaying(player)) {
                int playerNumber = 0;
                for (Integer integer : room.getPlayers().values()) {
                    if (integer != 0) {
                        playerNumber++;
                    }
                }
                return playerNumber + "";
            }
        }
        return null;
    }

}
