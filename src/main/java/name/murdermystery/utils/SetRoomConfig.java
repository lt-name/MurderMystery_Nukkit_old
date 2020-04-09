package main.java.name.murdermystery.utils;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.util.List;

public class SetRoomConfig {

    /**
     * 设置游戏地图出生点
     * @param player 玩家
     * @param config 配置文件
     */
    public static void setSpawn(Player player, Config config) {
        String spawn = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ()+ ":" + player.getLevel().getName();
        String world = player.getLevel().getName();
        config.set("World", world);
        config.set("出生点", spawn);
        config.save();
    }

    /**
     * 添加金锭生成点
     * @param player 玩家
     * @param config 配置文件
     */
    public static void addGoldSpawn(Player player, Config config) {
        addGoldSpawn(player.getFloorX(), player.getFloorY() + 1, player.getFloorZ(), config);
    }

    /**
     * @param x 金锭产出地点 X
     * @param y 金锭产出地点 Y
     * @param z 金锭产出地点 Z
     * @param config 配置文件
     */
    private static void addGoldSpawn(int x, int y, int z, Config config) {
        String s = x + ":" + y + ":" + z;
        List<String> list = config.getStringList("goldSpawn");
        list.add(s);
        config.set("goldSpawn", list);
        config.save();
    }

    /**
     * 设置等待时间
     * @param waitTime 等待时间
     * @param config 配置文件
     */
    public static void setWaitTime(Integer waitTime, Config config) {
        config.set("等待时间", waitTime);
        config.save();
    }

    /**
     * 设置游戏时间
     * @param gameTime 游戏时间
     * @param config 配置文件
     */
    public static void setGameTime(Integer gameTime, Config config) {
        config.set("游戏时间", gameTime);
        config.save();
    }

    /**
     * 设置金锭产出间隔
     * @param goldSpawnTime 等待时间
     * @param config 配置文件
     */
    public static void setGoldSpawnTime(Integer goldSpawnTime, Config config) {
        config.set("goldSpawnTime", goldSpawnTime);
        config.save();
    }

}
