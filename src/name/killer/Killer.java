package name.killer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.j256.ormlite.stmt.query.In;
import name.killer.Listener.GameLevel;
import name.killer.Listener.PlayerGame;
import name.killer.Listener.PlayerJoinAndQuit;
import name.killer.Room.Room;
import name.killer.Utils.SetRoomConfig;

import java.io.File;
import java.util.LinkedHashMap;


/**
 * @author lt_name
 */
public class Killer extends PluginBase {

    private static Killer killer;
    private Config config;
    private LinkedHashMap<String, Config> roomConfigs = new LinkedHashMap<>();
    private LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();
    private SetRoomConfig setRoomConfig;

    public static Killer getInstance() { return killer; }

    @Override
    public void onEnable() {
        killer = this;
        this.config = new Config(getDataFolder() + "/config.yml", 2);
        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        getServer().getPluginManager().registerEvents(new GameLevel(), this);
        getServer().getPluginManager().registerEvents(new PlayerGame(), this);
        File file = new File(this.getDataFolder() + "/Rooms");
        if (!file.exists() && !file.mkdirs()) {
            getLogger().error("Rooms 文件夹初始化失败");
        }
        this.setRoomConfig = new SetRoomConfig();
        getLogger().info("§a插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.config.save();
        for (Room gameRoom : this.rooms.values()) {
            gameRoom.getConfig().save();
        }
        getLogger().info("§c已卸载！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("killer")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length >0) {
                    switch (args[0]) {
                        case "join": case "加入":
                            if (args[1] != null) {


                            }else {
                                sender.sendMessage("/killer help 查看帮助");
                            }
                            break;
                        case "quit": case "退出":
                            for (Room gameRoom : this.rooms.values()) {
                                if (gameRoom.getPlayers().containsKey(player)) {
                                    gameRoom.quitRoom(player);
                                    player.sendMessage("你已退出房间");
                                }
                            }
                            break;
                        default:
                            player.sendMessage("killer--命令帮助");
                            player.sendMessage("killer 加入 房间号 加入游戏");
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
        }else if (command.getName().equals("kadmin")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length > 0) {
                    switch (args[0]) {
                        case "设置出生点": case "setspawn": case "SetSpawn":
                            setRoomConfig.setSpawn(player, getRoomConfig(player.getLevel()));
                            sender.sendMessage("出生点设置成功！");
                            break;
                        case "添加金锭生成点": case "addGoldSpawn":
                            setRoomConfig.addGoldSpawn(player, getRoomConfig(player.getLevel()));
                            sender.sendMessage("金锭生成点添加成功！");
                            break;
                        case "设置金锭产出间隔":
                            if (args.length == 2) {
                                setRoomConfig.setGoldSpawnTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                sender.sendMessage("金锭产出间隔已设置为：" + Integer.valueOf(args[1]));
                            }else {
                                sender.sendMessage("查看帮助：/kadmin help");
                            }
                            break;
                        case "设置等待时间":
                            if (args.length == 2) {
                                setRoomConfig.setWaitTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                sender.sendMessage("等待时间已设置为：" + Integer.valueOf(args[1]));
                            }else {
                                sender.sendMessage("查看帮助：/kadmin help");
                            }
                            break;
                        case "设置游戏时间":
                            if (args.length == 2) {
                                setRoomConfig.setGameTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                sender.sendMessage("游戏时间已设置为：" + Integer.valueOf(args[1]));
                            }else {
                                sender.sendMessage("查看帮助：/kadmin help");
                            }
                            break;
                        default:
                            player.sendMessage("§ekiller管理--命令帮助");
                            player.sendMessage("§a/设置出生点 §e设置当前位置为游戏出生点");
                            player.sendMessage("§a/添加金锭生成点 §e将当前位置设置为金锭生成点");
                            player.sendMessage("§a/设置等待时间 数字 §e设置游戏人数足够后的等待时间");
                            player.sendMessage("§a/设置游戏时间 数字 §e设置每轮游戏最长时间");
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

    public LinkedHashMap<String, Room> getRooms() {
        return this.rooms;
    }

    public Config getRoomConfig(Level level) {
        return getRoomConfig(level.getName());
    }

    public Config getRoomConfig(String level) {
        if (this.roomConfigs.containsKey(level)) {
            return this.roomConfigs.get(level);
        }
        if (!new File(getDataFolder() + "/Rooms/" + level + ".yml").exists()) {
            saveResource("room.yml", "/Rooms/" + level + ".yml", false);
        }
        Config config = new Config(getDataFolder() + "/Rooms/" + level + ".yml", 2);
        this.roomConfigs.put(level, config);
        return config;
    }

}
