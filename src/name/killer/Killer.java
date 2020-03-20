package name.killer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import name.killer.Listener.PlayerGame;
import name.killer.Listener.PlayerJoinAndQuit;

//import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lt_name
 */
public class Killer extends PluginBase {

    private static Killer killer;
    private List<Player> playing;
    private Map<Player, Integer> players;
    private List<String> spawn;
    private Config config;

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
        this.getLogger().info("§c已卸载！");
    }

    public static Killer getInstance() {
        return killer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("Killer") || command.getName().equals("杀手")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                switch (args[0]) {
                    case "加入":
                        if (!this.isPlaying(player)) {

                            this.addPlaying(player);
                        }else {
                            sender.sendMessage("§c你已经在游戏中，无法重复加入！");
                        }
                        break;
                    case "退出":
                        if (this.isPlaying(player)) {
                            this.delPlaying(player);
                            if (player.getGamemode() != 0) {
                                player.setGamemode(0);
                            }
                            if (player.getDataFlag(0, 5)) {
                                this.setPlayerInvisible(player, false);
                            }
                            player.teleport(getServer().getDefaultLevel().getSpawnLocation());
                            sender.sendMessage("已退出游戏！");
                        }else {
                            sender.sendMessage("你本来就不在游戏中，无需退出！");
                        }
                }
            }else {
                sender.sendMessage("请在游戏内输入！");
            }
            return true;
        }else if (command.getName().equals("kadmin") || command.getName().equals("杀手管理")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                switch (args[0]) {
                    case "addspawn":
                    case "AddSpawn":
                        if (spawn.size() >= 10) {
                            if (this.config.getString("World", null) == null) {
                                this.config.set("World", player.getLevel().getName());
                            }
                            String s = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ();
                            this.spawn.add(s);
                            this.config.set("出生点", this.spawn);
                            this.config.save();
                            sender.sendMessage(s + "已添加完成！");
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
                    default:
                        player.sendMessage("killer管理==命令帮助");
                        player.sendMessage("/kadmin addspawn 添加随机出生点");
                        player.sendMessage("/kadmin wait 设置等待地点");
                        break;
                }
            }else {
                sender.sendMessage("请在游戏内输入！");
            }
            return true;
        }
        return false;
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
        this.playing.add(player);
    }

    /**
     * @return boolean 玩家是否在游戏里
     * @param player 玩家
     */
    public boolean isPlaying(Player player) {
        return this.playing.contains(player);
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

    /**
     * @Description 开始游戏
     */
    public void startGame() {

    }

}
