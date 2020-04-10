package main.java.name.murdermystery.room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.tasks.WaitTask;
import main.java.name.murdermystery.utils.SavePlayerInventory;
import main.java.name.murdermystery.utils.Tools;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间实体类
 */
public class Room {

    private int mode; //0等待重置 1玩家等待中 2玩家游戏中 3胜利结算中
    public int waitTime, gameTime, victoryTime, goldSpawnTime, effectCD; //秒
    private int setWaitTime, setGameTime, setGoldSpawnTime;
    private LinkedHashMap<Player, Integer> players = new LinkedHashMap<>(); //0未分配 1平民 2侦探 3杀手
    private List<String> goldSpawn;
    private String spawn, world;

    /**
     * 初始化
     * @param config 配置文件
     */
    public Room(Config config) {
        this.setWaitTime = config.getInt("等待时间", 120);
        this.waitTime = this.setWaitTime;
        this.setGameTime = config.getInt("游戏时间", 600);
        this.gameTime = this.setGameTime;
        this.spawn = config.getString("出生点", null);
        this.goldSpawn = config.getStringList("goldSpawn");
        this.setGoldSpawnTime = config.getInt("goldSpawnTime", 15);
        this.goldSpawnTime = this.setGoldSpawnTime;
        this.victoryTime = 10;
        this.world = config.getString("World", null);
        this.mode = 0;
    }

    /**
     * 初始化Task
     */
    public void initTask() {
        if (this.getMode() == 0) {
            this.setMode(1);
            MurderMystery.getInstance().getServer().getScheduler().scheduleRepeatingTask(
                    MurderMystery.getInstance(), new WaitTask(MurderMystery.getInstance(), this), 20,true);
        }
    }

    /**
     * @param mode 房间状态
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return 房间状态
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * 结束本局游戏
     */
    public void endGame() {
        if (this.players.values().size() > 0 ) {
            Iterator<Map.Entry<Player, Integer>> it = this.players.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Player, Integer> entry = it.next();
                it.remove();
                quitRoom(entry.getKey());
            }
        }
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
        this.victoryTime = 10;
        this.goldSpawnTime = this.setGoldSpawnTime;
        Tools.cleanEntity(this.getWorld(), true);
        this.mode = 0;
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        if (this.players.values().size() < 16) {
            this.initTask();
            this.addPlaying(player);
            Tools.rePlayerState(player, false);
            SavePlayerInventory.savePlayerInventory(player, false);
            player.teleport(this.getSpawn());
        }
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    public void quitRoom(Player player) {
        this.quitRoom(player, true);
    }

    /**
     * 退出房间
     * @param player 玩家
     * @param online 是否在线
     */
    public void quitRoom(Player player, boolean online) {
        if (this.isPlaying(player)) {
            this.delPlaying(player);
        }
        if (online) {
            Tools.rePlayerState(player, true);
            SavePlayerInventory.savePlayerInventory(player, true);
            player.teleport(MurderMystery.getInstance().getServer().getDefaultLevel().getSafeSpawn());
        }
    }

    /**
     * 清空玩家背包
     * @param player 玩家
     */
    public void clearInventory(Player player) {
        player.getInventory().clearAll();
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     */
    public void addPlaying(Player player) {
        if (!this.players.containsKey(player)) {
            this.addPlaying(player, 0);
        }
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     * @param mode 身份
     */
    public void addPlaying(Player player, Integer mode) {
        if (mode == 1) {
            player.sendTitle("§a平民", "活下去，就是胜利", 10, 40, 10);
        }else if (mode == 2) {
            player.sendTitle("§e侦探", "找出杀手，并用弓箭击杀他", 10, 40, 10);
        }else if (mode == 3) {
            player.sendTitle("§c杀手", "杀掉所有人", 10, 40, 10);
        }
        this.players.put(player, mode);
    }

    /**
     * 删除记录
     * @param player 玩家
     */
    private void delPlaying(Player player) {
        this.players.remove(player);
    }

    /**
     * @return boolean 玩家是否在游戏里
     * @param player 玩家
     */
    public boolean isPlaying(Player player) {
        return this.players.containsKey(player);
    }

    /**
     * @return 玩家列表
     */
    public LinkedHashMap<Player, Integer> getPlayers() {
        return this.players;
    }

    /**
     * @return 玩家身份
     */
    public Integer getPlayerMode(Player player) {
        if (isPlaying(player)) {
            return this.players.get(player);
        }else {
            return null;
        }
    }

    /**
     * @return 出生点
     */
    public Position getSpawn() {
        String[] s = this.spawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                MurderMystery.getInstance().getServer().getLevelByName(s[3]));
    }

    /**
     * @return 金锭刷新时间
     */
    public int getGoldSpawnTime() {
        return this.setGoldSpawnTime;
    }

    /**
     * @return 等待时间
     */
    public int getWaitTime() {
        return this.setWaitTime;
    }

    public int getGameTime() {
        return this.setGameTime;
    }

    /**
     * @return 金锭产出地点
     */
    public List<String> getGoldSpawn() {
        return this.goldSpawn;
    }

    /**
     * @return 游戏世界
     */
    public Level getWorld() {
        return MurderMystery.getInstance().getServer().getLevelByName(this.world);
    }

}
