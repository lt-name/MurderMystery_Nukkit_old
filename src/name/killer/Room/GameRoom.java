package name.killer.Room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import name.killer.Killer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 房间实体类
 */
public class GameRoom {

    private int mode = 0; //0等待 1游戏中 2结算
    public int waitTime = 120;//秒
    public int gameTime = 600;
    public int victoryTime = 10;
    private Map<Player, Integer> players; //0平民 1侦探 2杀手 3观战 4未分配
    private List<String> spawn = new ArrayList<>();
    private List<String> goldSpawn = new ArrayList<>();
    public int goldSpawnTime = 15;
    private int goldSpawnTime1;
    private String Wait,World;

    /**
     * @Description 初始化
     * @param config 配置文件
     */
    public void init(Config config) {
        this.spawn = config.getStringList("出生点");
        this.Wait = config.getString("Wait", null);
        this.goldSpawn = config.getStringList("goldSpawn");
        this.goldSpawnTime = config.getInt("goldSpawnTime", 15);
        this.goldSpawnTime1 = config.getInt("goldSpawnTime", 15);
        this.World = config.getString("World", null);
    }

    /**
     * @return 房间状态
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * 开始游戏
     */
    public void startGame() {

    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public boolean joinRoom(Player player) {
        if (!this.isPlaying(player)) {
            if (this.players.size() <10) {
                this.addPlaying(player);
                player.teleport(this.getWait());
                return true;
            }
        }
        return false;
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    public boolean quitRoom(Player player) {
        if (this.isPlaying(player)) {
            this.delPlaying(player);
            player.teleport(Killer.getInstance().getServer().getDefaultLevel().getSafeSpawn());
            return true;
        }
        return false;
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     */
    public void addPlaying(Player player) {
        if (!this.players.containsKey(player)) {
            this.addPlaying(player, 4);
        }
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     * @param mode 身份
     */
    public void addPlaying(Player player, Integer mode) {
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
    public Map<Player, Integer> getPlayers() {
        return this.players;
    }

    /**
     * @return 玩家身份
     */
    public Integer getPlayerMode(Player player) {
        return this.players.getOrDefault(player, 4);
    }

    /**
     * @param spawn 金锭产出地点
     */
    public void setGoldSpawn(Vector3 spawn) {
        setGoldSpawn(spawn.x, spawn.y, spawn.z);
    }

    private void setGoldSpawn(double x, double y, double z) {
        setGoldSpawn((int)x, (int)y, (int)z);
    }

    /**
     * @param x 金锭产出地点 X
     * @param y 金锭产出地点 Y
     * @param z 金锭产出地点 Z
     */
    public void setGoldSpawn(int x, int y, int z) {
        String s = x + ":" + y + ":" + z;
        this.goldSpawn.add(s);
    }

    /**
     * @return 金锭刷新时间
     */
    public int getGoldSpawnTime() {
        return this.goldSpawnTime1;
    }

    /**
     * @return 金锭产出地点
     */
    public List<String> getGoldSpawn() {
        return this.goldSpawn;
    }

    /**
     * @return 等待地点
     */
    public Position getWait() {
        String[] W = this.Wait.split(":");
        return new Position(Integer.parseInt(W[0]),
                Integer.parseInt(W[1]),
                Integer.parseInt(W[2]),
                Killer.getInstance().getServer().getLevelByName(W[3]));
    }

    /**
     * @return 游戏世界
     */
    public Level getWorld() {
        return Killer.getInstance().getServer().getLevelByName(this.World);
    }

}
