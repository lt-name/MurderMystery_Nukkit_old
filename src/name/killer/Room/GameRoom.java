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

import static com.boydti.fawe.object.PseudoRandom.random;

/**
 * @Description 房间实体类
 */
public class GameRoom {

    private int mode = 0; //0等待 1游戏中 2需要重置参数
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
    public Boolean startGame() {
        List<String> player = null;
        for (Player p : this.players.keySet()) {
            player.add(p.getName());
        }
        if (player == null) { return false; }
        Player detective;
        Player killer = Killer.getInstance().getServer().getPlayer(player.get(random.nextInt(player.size())));
        do {
            detective = Killer.getInstance().getServer().getPlayer(player.get(random.nextInt(player.size())));
        }while (detective == killer);
        players.put(killer, 2);
        killer.sendMessage("你已成为杀手！");
        players.put(detective, 1);
        detective.sendMessage("你已成为侦探！");
        int i = 0;
        for (Player p : this.players.keySet()) {
            String[] s = this.spawn.get(i).split(":");
            p.teleport(new Position(Integer.parseInt(s[0]),
                    Integer.parseInt(s[1]),
                    Integer.parseInt(s[2]),
                    Killer.getInstance().getServer().getLevelByName(this.World)));
            i++;
            if (p == killer || p == detective) { continue; }
            players.put(p, 0);
            p.sendMessage("你已成为平民");
        }
        this.mode = 1;
        return true;
    }

    /**
     * 结束游戏
     * @param mode 胜利方
     */
    public void endGame(Integer mode) {
        String send;
        if (mode == 2) {
            send = "杀手取得胜利！";
        }else {
            send = "平民和侦探取得胜利！";
        }
        for (Player player : this.players.keySet()) {
            player.sendMessage(send);
        }
        this.endGame(false);
    }

    public void endGame(boolean timeOut) {
        for (Player player : this.players.keySet()) {
            if (timeOut) { player.sendMessage("时间耗尽，游戏结束！"); }
            if (player.getGamemode() != 0) { player.setGamemode(0); }
            player.teleport(Killer.getInstance().getServer().getDefaultLevel().getSafeSpawn());
            this.players.remove(player);
        }
        this.mode = 2;
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
