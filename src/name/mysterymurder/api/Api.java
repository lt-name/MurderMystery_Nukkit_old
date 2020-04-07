package name.mysterymurder.api;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import name.mysterymurder.MysteryMurder;
import name.mysterymurder.room.Room;

import java.util.LinkedHashMap;

public class Api {

    /**
     * @return 房间列表
     */
    public static LinkedHashMap<String, Room> getRooms() {
        return MysteryMurder.getInstance().getRooms();
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
        return getPlayerMode(MysteryMurder.getInstance().getServer().getPlayer(player));
    }

    /**
     * @param player 玩家
     * @return 玩家身份
     */
    public static String getPlayerMode(Player player) {
        for (Room room : getRooms().values()) {
            if (room.getMode() == 2) {
                if (room.isPlaying(player)) {
                    int mode = room.getPlayerMode(player);
                    switch (mode) {
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
        return "无";
    }

}
