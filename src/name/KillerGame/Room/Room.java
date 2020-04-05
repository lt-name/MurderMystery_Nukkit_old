package name.KillerGame.Room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import name.KillerGame.KillerGame;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 房间实体类
 */
public class Room {

    private int mode; //0等待重置 1玩家等待中 2玩家游戏中 3胜利结算中
    public int waitTime, gameTime, victoryTime = 10, goldSpawnTime; //秒
    private int setWaitTime, setGameTime, setGoldSpawnTime;
    private LinkedHashMap<Player, Integer> players = new LinkedHashMap<>(); //0未分配 1平民 2侦探 3杀手
    private List<String> goldSpawn;
    private String spawn, world;
    private Config config;

    /**
     * 初始化
     * @param config 配置文件
     */
    public Room(Config config) {
        this.config = config;
        this.waitTime = config.getInt("等待时间", 120);
        this.setWaitTime = this.waitTime;
        this.gameTime = config.getInt("游戏时间", 600);
        this.setGameTime = this.gameTime;
        this.spawn = config.getString("出生点", null);
        this.goldSpawn = config.getStringList("goldSpawn");
        this.goldSpawnTime = config.getInt("goldSpawnTime", 15);
        this.setGoldSpawnTime = this.goldSpawnTime;
        this.world = config.getString("World", null);
        this.mode = 0;
    }

    /**
     * @return 配置文件
     */
    public Config getConfig() {
        return this.config;
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
        for (Player player : this.players.keySet()) {
            quitRoom(player);
        }
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
        this.goldSpawnTime = this.setGoldSpawnTime;
        this.mode = 0;
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        if (player.getGamemode() != 0) {
            player.setGamemode(0);
        }
        rePlayerState(player);
        this.addPlaying(player);
        player.teleport(this.getSpawn());
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    public void quitRoom(Player player) {
        if (this.isPlaying(player)) {
            if (player.getGamemode() != 0) {
                player.setGamemode(0);
            }
            this.delPlaying(player);
            rePlayerState(player);
            player.teleport(KillerGame.getInstance().getServer().getDefaultLevel().getSafeSpawn());
        }
    }

    /**
     * 重置玩家状态
     * @param player 玩家
     */
    public void rePlayerState(Player player) {
        player.getInventory().clearAll();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
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
            player.sendTitle("§a你已被选为平民", "想办法活下去吧", 10, 40, 10);
        }else if (mode == 2) {
            player.sendTitle("§e你已被选为侦探", "想办法找出杀手吧", 10, 40, 10);
        }else if (mode == 3) {
            player.sendTitle("§c你已被选为杀手", "想办法杀光所有人吧", 10, 40, 10);
        }
        this.players.put(player, mode);
    }

    /**
     * 删除记录
     * @param player 玩家
     */
    public void delPlaying(Player player) {
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
                KillerGame.getInstance().getServer().getLevelByName(s[3]));
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
        return KillerGame.getInstance().getServer().getLevelByName(this.world);
    }

}
