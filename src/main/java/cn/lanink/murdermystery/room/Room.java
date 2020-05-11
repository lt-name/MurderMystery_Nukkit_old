package cn.lanink.murdermystery.room;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.tasks.TipsTask;
import cn.lanink.murdermystery.tasks.WaitTask;
import cn.lanink.murdermystery.utils.SavePlayerInventory;
import cn.lanink.murdermystery.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import tip.messages.NameTagMessage;
import tip.utils.Api;

import java.util.*;

/**
 * 房间类
 */
public class Room {

    private int mode; //0等待重置 1玩家等待中 2玩家游戏中 3胜利结算中
    public int waitTime, gameTime; //秒
    public int effectCD, swordCD, scanCD; //杀手技能CD
    public int victory; //胜利者
    private int setWaitTime, setGameTime, setGoldSpawnTime;
    private LinkedHashMap<Player, Integer> players = new LinkedHashMap<>(); //0未分配 1平民 2侦探 3杀手
    private LinkedHashMap<Player, Integer> skinNumber = new LinkedHashMap<>(); //玩家使用皮肤编号，用于防止重复使用
    private LinkedHashMap<Player, Skin> skinCache = new LinkedHashMap<>(); //缓存玩家皮肤，用于退出房间时还原
    private ArrayList<Position> randomSpawn = new ArrayList<>();
    private List<String> goldSpawn;
    private String spawn, world;
    public ArrayList<ArrayList<Vector3>> placeBlocks = new ArrayList<>();
    public ArrayList<String> task = new ArrayList<>();

    /**
     * 初始化
     * @param config 配置文件
     */
    public Room(Config config) {
        this.setWaitTime = config.getInt("等待时间", 120);
        this.setGameTime = config.getInt("游戏时间", 600);
        this.spawn = config.getString("出生点", null);
        this.goldSpawn = config.getStringList("goldSpawn");
        this.setGoldSpawnTime = config.getInt("goldSpawnTime", 15);
        this.world = config.getString("World", null);
        this.initTime();
        if (this.getLevel() == null) {
            Server.getInstance().loadLevel(this.world);
        }
        List<String> rSpawn = config.getStringList("randomSpawn");
        if (rSpawn.size() > 0) {
            for (String string : rSpawn) {
                String[] s = string.split(":");
                this.randomSpawn.add(new Position(
                        Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), this.getLevel()));
            }
        }
        this.loadChuck();
        this.mode = 0;
    }

    /**
     * 初始化Task
     */
    private void initTask() {
        this.setMode(1);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                MurderMystery.getInstance(), new WaitTask(MurderMystery.getInstance(), this), 20, true);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                MurderMystery.getInstance(), new TipsTask(MurderMystery.getInstance(), this), 20);
    }

    /**
     * 加载区块
     */
    private void loadChuck() {
        this.getLevel().loadChunk(this.getSpawn().getChunkX(), this.getSpawn().getChunkZ());
        if (this.randomSpawn.size() > 0) {
            for (Position position : this.randomSpawn) {
                this.getLevel().loadChunk(position.getChunkX(), position.getChunkZ());
            }
        }
    }

    /**
     * 初始化时间参数
     */
    public void initTime() {
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
        this.effectCD = 0;
        this.swordCD = 0;
        this.scanCD = 0;
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
        this.endGame(true);
    }

    /**
     * 结束本局游戏
     * @param normal 正常关闭
     */
    public void endGame(boolean normal) {
        this.mode = 0;
        if (normal) {
            if (this.players.values().size() > 0 ) {
                Iterator<Map.Entry<Player, Integer>> it = this.players.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<Player, Integer> entry = it.next();
                    it.remove();
                    this.quitRoom(entry.getKey());
                }
            }
        }else {
            this.getLevel().getPlayers().values().forEach(
                    player -> player.kick(MurderMystery.getInstance().getLanguage().roomSafeKick));
        }
        this.placeBlocks.forEach(list -> list.forEach(vector3 -> this.getLevel().setBlock(vector3, Block.get(0))));
        this.placeBlocks.clear();
        this.initTime();
        this.skinNumber.clear();
        this.skinCache.clear();
        this.task.clear();
        Tools.cleanEntity(this.getLevel(), true);
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        if (this.players.values().size() < 16) {
            this.loadChuck();
            if (this.mode == 0) {
                this.initTask();
            }
            this.addPlaying(player);
            Tools.rePlayerState(player, true);
            SavePlayerInventory.save(player);
            player.teleport(this.getSpawn());
            this.setRandomSkin(player, false);
            Tools.giveItem(player, 10);
            NameTagMessage nameTagMessage = new NameTagMessage(this.world, true, "");
            Api.setPlayerShowMessage(player.getName(), nameTagMessage);
            player.sendMessage(MurderMystery.getInstance().getLanguage().joinRoom.replace("%name%", this.world));
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
            Tools.removePlayerShowMessage(this.world, player);
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
            Tools.rePlayerState(player, false);
            SavePlayerInventory.restore(player);
            this.setRandomSkin(player, true);
        }else {
            this.skinNumber.remove(player);
            this.skinCache.remove(player);
        }
    }

    /**
     * 设置玩家随机皮肤
     * @param player 玩家
     * @param restore 是否为还原
     */
    public void setRandomSkin(Player player, boolean restore) {
        if (restore) {
            if (this.skinCache.containsKey(player)) {
                Tools.setPlayerSkin(player, this.skinCache.get(player));
                this.skinCache.remove(player);
            }
            this.skinNumber.remove(player);
        }else {
            for (Map.Entry<Integer, Skin> entry : MurderMystery.getInstance().getSkins().entrySet()) {
                if (!this.skinNumber.containsValue(entry.getKey())) {
                    this.skinCache.put(player, player.getSkin());
                    this.skinNumber.put(player, entry.getKey());
                    Tools.setPlayerSkin(player, entry.getValue());
                    return;
                }
            }
        }
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
                this.getLevel());
    }

    /**
     * @return 随机出生点列表
     */
    public List<Position> getRandomSpawn() {
        return this.randomSpawn;
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

    /**
     * @return 游戏时间
     */
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
    public Level getLevel() {
        return Server.getInstance().getLevelByName(this.world);
    }

    /**
     * 获取玩家在游戏中使用的皮肤
     * @param player 玩家
     * @return 皮肤
     */
    public Skin getPlayerSkin(Player player) {
        if (this.skinNumber.containsKey(player)) {
            return MurderMystery.getInstance().getSkins().get(this.skinNumber.get(player));
        }
        return player.getSkin();
    }

}
