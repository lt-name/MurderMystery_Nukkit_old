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
        for (Room room : getRooms().values()) {
            if (room.getMode() == 2) {
                if (room.isPlaying(player)) {
                    switch (room.getPlayerMode(player)) {
                        case 1:
                            return "平民";
                        case 2:
                            return "侦探";
                        case 3:
                            return "杀手";
                        default:
                            return "死亡";
                    }
                }
            }
        }
        return "未分配";
    }

    /**
     * 根据玩家获取倒计时
     * @param player 玩家
     * @return 剩余时间
     */
    public static Integer getTime(Player player) {
        for (Room room : getRooms().values()) {
            if (room.isPlaying(player)) {
                if (room.getMode() == 1) {
                    return room.waitTime;
                }else if (room.getMode() == 2) {
                    return room.gameTime;
                }
            }
        }
        return null;
    }

    /**
     * 获取存活玩家数量
     * @param player 玩家
     * @return 存活玩家数量
     */
    public static Integer getSurvivor(Player player) {
        for (Room room : getRooms().values()) {
            if (room.isPlaying(player)) {
                int playerNumber = 0;
                for (Integer integer : room.getPlayers().values()) {
                    if (integer != 0) {
                        playerNumber++;
                    }
                }
                return playerNumber;
            }
        }
        return null;
    }

}
