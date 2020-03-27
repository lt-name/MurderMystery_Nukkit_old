package name.killer.Room;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import name.killer.Killer;
import name.killer.Tasks.ResetLevelTask;
import name.killer.Utils.LevelFileReset;

import java.util.LinkedHashMap;
import java.util.List;

import static com.boydti.fawe.object.PseudoRandom.random;

/**
 * 房间实体类
 */
public class GameRoom {

    private int mode; //0等待重置 1玩家等待中 2玩家游戏中
    public int waitTime, gameTime; //秒
    //public int victoryTime = 10;
    private LinkedHashMap<Player, Integer> players = new LinkedHashMap<>(); //0未分配 1平民 2侦探 3杀手 4等待重生
    private LinkedHashMap<Player, Integer> playerSpawnTime = new LinkedHashMap<>();
    private List<String> goldSpawn;
    public int goldSpawnTime;
    private int setGoldSpawnTime;
    private String spawn, world;
    private Config config;

    /**
     * 初始化
     * @param config 配置文件
     */
    public GameRoom(Config config) {
        this.config = config;
        this.waitTime = config.getInt("等待时间", 120);
        this.gameTime = config.getInt("游戏时间", 600);
        this.spawn = config.getString("出生点", null);
        this.goldSpawn = config.getStringList("goldSpawn");
        this.goldSpawnTime = config.getInt("goldSpawnTime", 15);
        this.setGoldSpawnTime = config.getInt("goldSpawnTime", 15);
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
            i++;
            if (p == killer || p == detective) { continue; }
            players.put(p, 0);
            p.sendMessage("你已成为平民");
        }
        this.mode = 2;
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
        this.mode = 0;
    }

    public void endGame(boolean timeOut) {
        for (Player player : this.players.keySet()) {
            if (timeOut) {
                player.sendMessage("时间耗尽，游戏结束！");
            }
            quitRoom(player);
        }
        this.mode = 0;
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public boolean joinRoom(Player player) {
        if (this.mode == 0) {
            Killer.getInstance().getServer().getScheduler().scheduleTask(
                    Killer.getInstance(), new ResetLevelTask(Killer.getInstance(), this), true);
        }else if (this.mode == 1) {
            if (!this.isPlaying(player)) {
                if (this.players.size() < 10) {
                    this.addPlaying(player);
                    player.teleport(this.getSpawn());
                    return true;
                }
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
            if (player.getGamemode() != 0) {
                player.setGamemode(0);
            }
            player.getInventory().clearAll();
            player.teleport(Killer.getInstance().getServer().getDefaultLevel().getSafeSpawn());
            this.delPlaying(player);
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
    public LinkedHashMap<Player, Integer> getPlayers() {
        return this.players;
    }

    /**
     * @return 玩家重生时间
     */
    public LinkedHashMap<Player, Integer> getPlayerSpawnTime() {
        return this.playerSpawnTime;
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
     * @param gameTime 游戏时间
     */
    public boolean setGameTime(int gameTime) {
        if (this.mode == 0) {
            this.gameTime = gameTime;
            this.config.set("游戏时间", this.gameTime);
            this.config.save();
            return true;
        }
        return false;
    }

    /**
     * @param waitTime 等待时间
     */
    public boolean setWaitTime(int waitTime) {
        if (this.mode == 0) {
            this.waitTime = waitTime;
            this.config.set("等待时间", this.waitTime);
            this.config.save();
            return true;
        }
        return false;
    }

    /**
     * @param player 玩家
     */
    public boolean setSpawn(Player player) {
        if (this.mode == 0) {
            this.spawn = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ()+ ":" + player.getLevel().getName();
            this.world = player.getLevel().getName();
            this.config.set("World", this.world);
            this.config.set("出生点", this.spawn);
            this.config.save();
            return true;
        }
        return false;
    }

    /**
     * @return 出生点
     */
    public Position getSpawn() {
        String[] s = this.spawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                Killer.getInstance().getServer().getLevelByName(s[3]));
    }

    /**
     * @param player 玩家
     */
    public boolean setGoldSpawn(Player player) {
        if (this.mode == 0) {
            setGoldSpawn(player.getFloorX(), player.getFloorY(), player.getFloorZ());
            return true;
        }
        return false;
    }

    /**
     * @param x 金锭产出地点 X
     * @param y 金锭产出地点 Y
     * @param z 金锭产出地点 Z
     */
    private void setGoldSpawn(int x, int y, int z) {
        String s = x + ":" + y + ":" + z;
        this.goldSpawn.add(s);
        this.config.set("goldSpawn", this.goldSpawn);
        this.config.save();
    }

    /**
     * @return 金锭刷新时间
     */
    public int getGoldSpawnTime() {
        return this.setGoldSpawnTime;
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
        return Killer.getInstance().getServer().getLevelByName(this.world);
    }

}
