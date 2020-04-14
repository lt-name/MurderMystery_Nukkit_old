package main.java.name.murdermystery;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import main.java.name.murdermystery.api.Api;
import main.java.name.murdermystery.listener.GuiListener;
import main.java.name.murdermystery.listener.PlayerGame;
import main.java.name.murdermystery.listener.PlayerJoinAndQuit;
import main.java.name.murdermystery.listener.RoomLevelProtection;
import main.java.name.murdermystery.room.Room;
import main.java.name.murdermystery.ui.GuiCreate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MurderMystery
 * @author lt_name
 */
public class MurderMystery extends PluginBase {

    private static MurderMystery murderMystery;
    private Config config;
    private LinkedHashMap<String, Config> roomConfigs = new LinkedHashMap<>();
    private LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Skin> skins = new LinkedHashMap<>();

    public static MurderMystery getInstance() { return murderMystery; }

    @Override
    public void onEnable() {
        if (murderMystery == null) {
            murderMystery = this;
        }
        this.config = new Config(getDataFolder() + "/config.yml", 2);
        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        getServer().getPluginManager().registerEvents(new RoomLevelProtection(), this);
        getServer().getPluginManager().registerEvents(new PlayerGame(), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        File file1 = new File(this.getDataFolder() + "/Rooms");
        File file2 = new File(this.getDataFolder() + "/PlayerInventory");
        File file3 = new File(this.getDataFolder() + "/Skins");
        if (!file1.exists() && !file1.mkdirs()) {
            getLogger().error("Rooms 文件夹初始化失败");
        }
        if (!file2.exists() && !file2.mkdirs()) {
            getLogger().error("PlayerInventory 文件夹初始化失败");
        }
        if (!file3.exists() && !file3.mkdirs()) {
            getLogger().error("Skins 文件夹初始化失败");
        }
        getLogger().info("§a开始加载房间");
        this.loadRooms();
        getLogger().info("§a开始加载皮肤");
        this.loadSkins();
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI api = PlaceholderAPI.getInstance();
            api.visitorSensitivePlaceholder("MurderPlayerMode", (player, placeholderParameters) -> Api.getPlayerMode(player), 20, true);
            api.visitorSensitivePlaceholder("MurderTime", (player, placeholderParameters) -> Api.getTime(player), 20, true);
            api.visitorSensitivePlaceholder("MurderSurvivorNumber", (player, placeholderParameters) -> Api.getSurvivor(player), 20, true);
            api.visitorSensitivePlaceholder("MurderRoomMode", (player, placeholderParameters) -> Api.getRoomMode(player), 20, true);
        }
        getLogger().info("§a插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.config.save();
        if (this.rooms.values().size() > 0) {
            Iterator<Map.Entry<String, Room>> it = this.rooms.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Room> entry = it.next();
                if (entry.getValue().getPlayers().size() > 0) {
                    entry.getValue().endGame(false);
                    getLogger().info("§c房间：" + entry.getKey() + " 非正常结束！");
                }else {
                    getLogger().info("§c房间：" + entry.getKey() + " 已卸载！");
                }
                it.remove();
            }
        }
        this.rooms.clear();
        this.roomConfigs.clear();
        this.skins.clear();
        getLogger().info("§c插件卸载完成！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("killer")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (args.length > 0) {
                    switch (args[0]) {
                        case "join": case "加入":
                            if (this.rooms.size() > 0) {
                                for (Room room : this.rooms.values()) {
                                    if (room.isPlaying(player)) {
                                        sender.sendMessage("§c你已经在一个房间中了!");
                                        return true;
                                    }
                                }
                                for(Entity entity : player.getLevel().getEntities()) {
                                    if (entity.isPassenger(player)) {
                                        sender.sendMessage("§a请勿在骑乘状态下进入房间！");
                                        return true;
                                    }
                                }
                                if (args.length < 2) {
                                    for (Room room : this.rooms.values()) {
                                        if (room.getMode() == 0 || room.getMode() == 1) {
                                            room.joinRoom(player);
                                        }
                                    }
                                    sender.sendMessage("§a已为你随机分配房间！");
                                }else if (this.rooms.containsKey(args[1])) {
                                    Room room = this.rooms.get(args[1]);
                                    if (room.getMode() == 2 || room.getMode() == 3) {
                                        sender.sendMessage("§a该房间正在游戏中，请稍后");
                                    }else if (room.getPlayers().values().size() > 15) {
                                        sender.sendMessage("§a该房间已满人，请稍后");
                                    } else {
                                        room.joinRoom(player);
                                    }
                                }else {
                                    sender.sendMessage("§a该房间不存在！");
                                }
                            }else {
                                sender.sendMessage("§a暂无房间可用！");
                            }
                            break;
                        case "quit": case "退出":
                            for (Room gameRoom : this.rooms.values()) {
                                if (gameRoom.getPlayers().containsKey(player)) {
                                    gameRoom.quitRoom(player, true);
                                }
                            }
                            sender.sendMessage("§a你已退出房间");
                            break;
                        case "list": case "列表":
                            StringBuilder list = new StringBuilder().append("§e房间列表： §a");
                            for (String string : this.rooms.keySet()) {
                                list.append(string).append(" ");
                            }
                            sender.sendMessage(String.valueOf(list));
                            break;
                        default:
                            sender.sendMessage("§e/killer--命令帮助");
                            sender.sendMessage("§e/killer §e打开ui");
                            sender.sendMessage("§a/killer join 房间名称 §e加入游戏");
                            sender.sendMessage("§a/killer quit §e退出游戏");
                            sender.sendMessage("§a/killer list §e查看房间列表");
                            break;
                    }
                }else {
                    GuiCreate.sendUserMenu(player);
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
                            this.roomSetSpawn(player, getRoomConfig(player.getLevel()));
                            sender.sendMessage("§a出生点设置成功！");
                            break;
                        case "添加金锭生成点": case "addGoldSpawn":
                            this.roomAddGoldSpawn(player, getRoomConfig(player.getLevel()));
                            sender.sendMessage("§a金锭生成点添加成功！");
                            break;
                        case "设置金锭产出间隔":
                            if (args.length == 2) {
                                if (args[1].matches("[0-9]*")) {
                                    this.roomSetGoldSpawnTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                    sender.sendMessage("§a金锭产出间隔已设置为：" + Integer.valueOf(args[1]));
                                }else {
                                    sender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                sender.sendMessage("§a查看帮助：/kadmin help");
                            }
                            break;
                        case "设置等待时间":
                            if (args.length == 2) {
                                if (args[1].matches("[0-9]*")) {
                                    this.roomSetWaitTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                    sender.sendMessage("§a等待时间已设置为：" + Integer.valueOf(args[1]));
                                }else {
                                    sender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                sender.sendMessage("§a查看帮助：/kadmin help");
                            }
                            break;
                        case "设置游戏时间":
                            if (args.length == 2) {
                                if (args[1].matches("[0-9]*")) {
                                    if (Integer.parseInt(args[1]) > 60) {
                                        this.roomSetGameTime(Integer.valueOf(args[1]), getRoomConfig(player.getLevel()));
                                        sender.sendMessage("§a游戏时间已设置为：" + Integer.valueOf(args[1]));
                                    } else {
                                        sender.sendMessage("§a游戏时间最小不能低于1分钟！");
                                    }
                                }else {
                                    sender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                sender.sendMessage("§a查看帮助：/kadmin help");
                            }
                            break;
                        case "reload": case "重载":
                            this.reLoadRooms();
                            sender.sendMessage("§a配置重载完成！");
                            break;
                        case "unload":
                            this.unloadRooms();
                            sender.sendMessage("§a已卸载所有房间！");
                            break;
                        default:
                            sender.sendMessage("§e killer管理--命令帮助");
                            sender.sendMessage("§a/kadmin §e打开ui");
                            sender.sendMessage("§a/kadmin 设置出生点 §e设置当前位置为游戏出生点");
                            sender.sendMessage("§a/kadmin 添加金锭生成点 §e将当前位置设置为金锭生成点");
                            sender.sendMessage("§a/kadmin 设置金锭产出间隔 数字 §e设置金锭生成间隔");
                            sender.sendMessage("§a/kadmin 设置等待时间 数字 §e设置游戏人数足够后的等待时间");
                            sender.sendMessage("§a/kadmin 设置游戏时间 数字 §e设置每轮游戏最长时间");
                            sender.sendMessage("§a/kadmin reload §e重载所有房间");
                            sender.sendMessage("§a/kadmin unload §e关闭所有房间,并卸载配置");
                            break;
                    }
                }else {
                    GuiCreate.sendAdminMenu(player);
                }
            }else {
                if(args.length > 0 && args[0].equals("unload")) {
                    this.unloadRooms();
                    sender.sendMessage("§a已卸载所有房间！");
                }else {
                    sender.sendMessage("§a请在游戏内输入！");
                }
            }
            return true;
        }
        return false;
    }

    public Config getConfig() {
        return this.config;
    }

    public boolean getActionBar() {
        return this.config.getBoolean("底部显示信息", true);
    }

    public LinkedHashMap<String, Room> getRooms() {
        return this.rooms;
    }

    private Config getRoomConfig(Level level) {
        return getRoomConfig(level.getName());
    }

    private Config getRoomConfig(String level) {
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

    public LinkedHashMap<Integer, Skin> getSkins() {
        return this.skins;
    }

    /**
     * 加载所有房间
     */
    private void loadRooms() {
        File[] s = new File(getDataFolder() + "/Rooms").listFiles();
        if (s != null) {
            for (File file1 : s) {
                String[] fileName = file1.getName().split("\\.");
                if (fileName.length > 0) {
                    Room room = new Room(getRoomConfig(fileName[0]));
                    this.rooms.put(fileName[0], room);
                    getLogger().info("§a房间：" + fileName[0] + " 已加载！");
                }
            }
        }
    }

    /**
     * 卸载所有房间
     */
    private void unloadRooms() {
        if (this.rooms.values().size() > 0) {
            Iterator<Map.Entry<String, Room>> it = this.rooms.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Room> entry = it.next();
                entry.getValue().endGame();
                getLogger().info("§c房间：" + entry.getKey() + " 已卸载！");
                it.remove();
            }
            this.rooms.clear();
        }
        if (this.roomConfigs.values().size() > 0) {
            this.roomConfigs.clear();
        }
    }

    /**
     * 重载所有房间
     */
    private void reLoadRooms() {
        this.unloadRooms();
        this.loadRooms();
    }

    /**
     * 加载所有皮肤
     */
    private void loadSkins() {
        File[] files = (new File(getDataFolder() + "/Skins")).listFiles();
        if (files != null && files.length > 0) {
            int x = 0;
            for (File file : files) {
                String skinName = file.getName();
                File skinFile = new File(getDataFolder() + "/Skins/" + skinName + "/skin.png");
                if (skinFile.exists()) {
                    Skin skin = new Skin();
                    BufferedImage skinData = null;
                    try {
                        skinData = ImageIO.read(skinFile);
                    } catch (IOException e) {
                        System.out.println(skinName + "加载失败");
                    }
                    if (skinData != null) {
                        skin.setSkinData(skinData);
                        skin.setSkinId(skinName);
                        getLogger().info("编号： " + x + " 皮肤： " + skinName + " 已加载");
                        this.skins.put(x, skin);
                        x++;
                    }else {
                        getLogger().warning(skinName + "加载失败，这可能不是一个正确的图片");
                    }
                } else {
                    getLogger().warning(skinName + "加载失败，请将皮肤文件命名为 skin.png");
                }
            }
        }
        if (this.skins.size() > 15) {
            getLogger().info("§a皮肤加载完成！");
        }else {
            getLogger().warning("§c当前皮肤数量小于16，部分玩家仍可使用自己的皮肤");
        }
    }

    private void roomSetSpawn(Player player, Config config) {
        String spawn = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ()+ ":" + player.getLevel().getName();
        String world = player.getLevel().getName();
        config.set("World", world);
        config.set("出生点", spawn);
        config.save();
    }

    private void roomAddGoldSpawn(Player player, Config config) {
        this.roomAddGoldSpawn(player.getFloorX(), player.getFloorY() + 1, player.getFloorZ(), config);
    }

    private void roomAddGoldSpawn(int x, int y, int z, Config config) {
        String s = x + ":" + y + ":" + z;
        List<String> list = config.getStringList("goldSpawn");
        list.add(s);
        config.set("goldSpawn", list);
        config.save();
    }

    private void roomSetWaitTime(Integer waitTime, Config config) {
        config.set("等待时间", waitTime);
        config.save();
    }

    private void roomSetGameTime(Integer gameTime, Config config) {
        config.set("游戏时间", gameTime);
        config.save();
    }

    private void roomSetGoldSpawnTime(Integer goldSpawnTime, Config config) {
        config.set("goldSpawnTime", goldSpawnTime);
        config.save();
    }

}
