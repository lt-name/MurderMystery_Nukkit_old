package name.killer;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import name.killer.Listener.PlayerGame;
import name.killer.Listener.PlayerJoinAndQuit;
import name.killer.Room.GameRoom;
import name.killer.Tasks.GameTask;

import java.io.File;
import java.util.LinkedHashMap;


/**
 * @author lt_name
 */
public class Killer extends PluginBase {

    private static Killer killer;
    private Config config;
    private LinkedHashMap<String, GameRoom> rooms;
    private LinkedHashMap<String, Task> roomTask;

    public static Killer getInstance() { return killer; }

    @Override
    public void onEnable() {
        if (killer == null) { killer = this; }
        this.config = new Config(getDataFolder() + "/config.yml", 2);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGame(), this);
        File file = new File(this.getDataFolder() + "/Rooms");
        if (!file.exists() && !file.mkdirs()) {
            this.getLogger().warning("Rooms 文件夹初始化失败");
        }
        this.init(file);
        this.getLogger().info("§a插件加载完成！");
    }

    private void init(File rooms) {
        if (rooms.isDirectory()) {
            File[] files = rooms.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        this.rooms.put(file.getName(), new GameRoom(new Config(file, 2)));
                        Task RoomTask = new GameTask(this, this.rooms.get(file.getName()));
                        this.roomTask.put(file.getName(), RoomTask);
                        this.getServer().getScheduler().scheduleRepeatingTask(
                                this, RoomTask, 20, true);
                    }
                }
            }
        }
    }

    private void init(Level level) {
        this.rooms.put(level.getName(), new GameRoom(new Config(getDataFolder() + "/Rooms/" + level.getName() +".yml", 2)));
        Task RoomTask = new GameTask(this, this.rooms.get(level.getName()));
        this.roomTask.put(level.getName(), RoomTask);
        this.getServer().getScheduler().scheduleRepeatingTask(
                this, RoomTask, 20, true);
    }


    @Override
    public void onDisable() {
        this.config.save();
        for (GameRoom gameRoom : this.rooms.values()) {
            gameRoom.getConfig().save();
        }
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
                            if (args[1] != null) {

/*                                if (gameRoom.getPlayers().size() < 10) {
                                    if (gameRoom.joinRoom(player)) {
                                        player.sendMessage("成功加入房间！");
                                        return true;
                                    }
                                }*/
                            }else {
                                sender.sendMessage("/killer help 查看帮助");
                            }
                            break;
                        case "quit":
                        case "退出":
                            for (GameRoom gameRoom : this.rooms.values()) {
                                if (gameRoom.getPlayers().containsKey(player)) {
                                    if (gameRoom.quitRoom(player)) {
                                        player.sendMessage("你已退出房间");
                                    }
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
        }else if (command.getName().equals("kadmin") || command.getName().equals("杀手管理")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length > 0) {
                    switch (args[0]) {
                        case "addspawn":
                        case "AddSpawn":
                        case "添加出生点":
                            if (!this.rooms.containsKey(player.getLevel().getName())) {
                                init(player.getLevel());
                            }
                            GameRoom gameRoom = this.rooms.get(player.getLevel().getName());
                            if (gameRoom.getSpawn().size() < 10) {
                                String s = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ();
                                gameRoom.getSpawn().add(s);
                                gameRoom.getConfig().save();
                                sender.sendMessage(player.getLevel().getName() + ":" + s + "已添加完成！");
                            }else {
                                sender.sendMessage("出生点只能设置10个");
                            }
                            break;
                        case "wait":
                        case "Wait":
                            this.rooms.get(player.getLevel().getName()).setWait(player);
                            sender.sendMessage("已设置等待地点！");
                            break;
                        case "start":
                        case "开始":
                            if (args[1] != null) {
                                if (this.rooms.get(player.getLevel().getName()).startGame()) {
                                    player.sendMessage(player.getLevel().getName() + "已开始");
                                }else {
                                    player.sendMessage("房间未满足条件！");
                                }
                            }else {
                                player.sendMessage("/kadmin start 房间名称");
                            }
                            break;
                        default:
                            player.sendMessage("killer管理--命令帮助");
                            player.sendMessage("/kadmin addspawn 添加随机出生点");
                            player.sendMessage("/kadmin wait 设置等待地点");
                            player.sendMessage("/kadmin start 房间 房间开始游戏");
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

    public LinkedHashMap<String, GameRoom> getRooms() {
        return this.rooms;
    }

}
