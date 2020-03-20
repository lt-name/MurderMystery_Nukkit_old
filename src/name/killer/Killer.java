package name.killer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import name.killer.Listener.PlayerGame;
import name.killer.Listener.PlayerJoinAndQuit;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.boydti.fawe.object.PseudoRandom.random;


/**
 * @author lt_name
 */
public class Killer extends PluginBase {

    private static Killer killer;
    private List<Player> playing = new ArrayList<>();
    private Map<Player, Integer> players; //0 平民 1侦探 2杀手 3观战
    private List<String> spawn = new ArrayList<>();
    private Config config;
    public int number = 0;

    public static Killer getInstance() { return killer; }

    @Override
    public void onEnable() {
        if (killer == null) { killer = this; }
        this.spawn = new Config(getDataFolder() + "/config.yml", 2).getStringList("出生点");
        this.config = new Config(getDataFolder() + "/config.yml", 2);
        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerGame(), this);
        this.getLogger().info("§a插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.config.set("出生点", this.spawn);
        this.config.save();
        this.getLogger().info("§c已卸载！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("killer") || command.getName().equals("杀手")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length >0) {
                    switch (args[0]) {
                        case "join":
                        case "加入":
                            if (!this.isPlaying(player)) {
                                if (this.playing.size() <= 10) {
                                    String[] Wait = this.getWait().split(":");
                                    player.teleport(new Position(Integer.parseInt(Wait[0]),
                                            Integer.parseInt(Wait[1]),
                                            Integer.parseInt(Wait[2]),
                                            getServer().getLevelByName(Wait[3])));
                                    this.addPlaying(player);
                                    sender.sendMessage("你已成功加入游戏！");
                                }else {
                                    sender.sendMessage("当前仅支持10人同时游戏！");
                                }
                            }else {
                                sender.sendMessage("§c你已经在游戏中，无法重复加入！");
                            }
                            break;
                        case "quit":
                        case "退出":
                            if (this.isPlaying(player)) {
                                this.delPlaying(player);
                                if (player.getGamemode() != 0) {
                                    player.setGamemode(0);
                                }
                           /* if (player.getDataFlag(0, 5)) {
                                this.setPlayerInvisible(player, false);
                            }*/
                                player.teleport(getServer().getDefaultLevel().getSpawnLocation());
                                sender.sendMessage("已退出游戏！");
                            }else {
                                sender.sendMessage("你本来就不在游戏中，无需退出！");
                            }
                        default:
                            player.sendMessage("killer--命令帮助");
                            player.sendMessage("killer 加入 加入游戏");
                            player.sendMessage("killer 退出 退出游戏");
                            break;
                    }
                }else {
                    sender.sendMessage("/killer help 查看帮助");
                }
            }else {
                sender.sendMessage("请在游戏内输入！");
            }
            return true;
        }else if (command.getName().equals("kadmin") || command.getName().equals("杀手管理")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length > 0) {
                    switch (args[0]) {
                        case "addspawn":
                        case "AddSpawn":
                            if (spawn.size() < 10) {
                                if (this.config.getString("World", null) == null) {
                                    this.config.set("World", player.getLevel().getName());
                                    this.config.save();
                                }
                                if (this.getWorld() == null || this.getWorld().equals(player.getLevel().getName())) {
                                    String s = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ();
                                    this.spawn.add(s);
                                    this.config.set("出生点", this.spawn);
                                    this.config.save();
                                    sender.sendMessage(s + "已添加完成！");
                                }else {
                                    sender.sendMessage("请在同一个世界设置出生点");
                                }
                            }else {
                                sender.sendMessage("随机出生点只能设置10个");
                            }
                            break;
                        case "wait":
                        case "Wait":
                            String Wait = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ()+ ":" + player.getLevel().getName();
                            this.config.set("Wait", Wait);
                            this.config.save();
                            sender.sendMessage("已设置等待地点！");
                            break;
                        case "start":
                        case "开始":
                            this.startGame();
                            break;
                        default:
                            player.sendMessage("killer管理--命令帮助");
                            player.sendMessage("/kadmin addspawn 添加随机出生点");
                            player.sendMessage("/kadmin wait 设置等待地点");
                            break;
                    }
                }else {
                    sender.sendMessage("/kadmin help 查看帮助");
                }
            }else {
                sender.sendMessage("请在游戏内输入！");
            }
            return true;
        }
        return false;
    }

    /**
     * @Description 开始游戏
     */
    public void startGame() {
        Player detective;
        Player killer = this.playing.get(random.nextInt(playing.size()));
        do {
            detective = this.playing.get(random.nextInt(playing.size()));
        }while (detective == killer);
        players.put(killer, 2);
        killer.sendMessage("你已成为杀手！");
        players.put(detective, 1);
        detective.sendMessage("你已成为侦探！");
        int i=0;
        this.number = 0;
        for (Player player : playing) {
            String[] s = this.spawn.get(i).split(":");
            player.teleport(new Position(Integer.parseInt(s[0]),
                    Integer.parseInt(s[1]),
                    Integer.parseInt(s[2]),
                    getServer().getLevelByName(this.getWorld())));
            i++;
            this.number++;
            if (player == killer || player == detective) {
                continue;
            }
            players.put(player, 0);
            player.sendMessage("你已成为平民");
        }

    }

    /**
     * @Description 结束游戏
     */
    public void endGame() {
        for (Player player : this.playing) {
            if (player.getGamemode() != 0) {
                player.setGamemode(0);
            }
            this.delPlaying(player);
            this.players.remove(player);
            player.sendMessage("本轮游戏结束！");
            player.teleport(getServer().getDefaultLevel().getSafeSpawn());
        }
    }

    /**
     * @Description 记录在游戏内的玩家
     * @param player 玩家
     */
    public void addPlaying(Player player) {
        this.playing.add(player);
    }

    /**
     * @Description 删除记录
     * @param player 玩家
     */
    public void delPlaying(Player player) {
        this.playing.remove(player);
        this.players.remove(player);
    }

    /**
     * @return boolean 玩家是否在游戏里
     * @param player 玩家
     */
    public boolean isPlaying(Player player) {
        return this.playing.contains(player);
    }

    /**
     * @return 玩家列表
     */
    public List<Player> getPlayerList() {
        return this.playing;
    }

    /**
     * @return 游戏地图
     */
    public String getWorld() {
        return this.config.get("World", null);
    }

    /**
     * @return 等待地点
     */
    public String getWait() {
        return this.config.getString("Wait", null);
    }

    /**
     * @return 玩家职位
     * @param player 玩家
     */
    public Integer getPlayerMode(Player player) {
        return players.get(player);
    }

    /**
     * @Description 设置玩家隐身
     * @param player 玩家
     * @param bool 是否设置隐身
     */
    public void setPlayerInvisible(Player player, boolean bool) {
        player.setDataFlag(0, 5, bool);
        player.setNameTagVisible(!bool);
    }

}
