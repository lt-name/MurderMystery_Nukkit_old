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
        if (this.players.values().size() > 0 ){
            for (Player player : this.players.keySet()) {
                quitRoom(player);
            }
        }
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
        this.victoryTime = 10;
        this.goldSpawnTime = this.setGoldSpawnTime;
        this.mode = 0;
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        this.addPlaying(player);
        this.rePlayerState(player);
        this.setNameTagVisible(player, false);
        player.teleport(this.getSpawn());
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    public void quitRoom(Player player) {
        if (this.isPlaying(player)) {
            this.delPlaying(player);
            this.rePlayerState(player);
            this.setNameTagVisible(player, true);
            player.teleport(KillerGame.getInstance().getServer().getDefaultLevel().getSafeSpawn());
        }
    }

    /**
     * 设置玩家名称是否可见
     * @param player 玩家
     * @param canSee 是否可见
     */
    private void setNameTagVisible(Player player, boolean canSee) {
        player.setNameTagAlwaysVisible(canSee);
        player.setNameTagVisible(canSee);
    }

    /**
     * 重置玩家状态
     * @param player 玩家
     */
    public void rePlayerState(Player player) {
        if (player.getGamemode() != 0) {
            player.setGamemode(0);
        }
        if (player.isSprinting()) {
            player.setMovementSpeed(0.13F);
        }else {
            player.setMovementSpeed(0.1F);
        }
        this.clearInventory(player);
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
    }

    public void clearInventory(Player player) {
        player.getInventory().close(player);
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
            player.sendTitle("§e侦探", "找出杀手，并用弓箭击中他", 10, 40, 10);
        }else if (mode == 3) {
            player.sendTitle("§c杀手", "杀掉所有人", 10, 40, 10);
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
