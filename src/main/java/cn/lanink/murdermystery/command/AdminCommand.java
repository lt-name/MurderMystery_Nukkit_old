package cn.lanink.murdermystery.command;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.ui.GuiCreate;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class AdminCommand extends Command {

    MurderMystery murderMystery = MurderMystery.getInstance();
    private final String name;

    public AdminCommand(String name) {
        super(name, "MurderMystery 管理命令", "/" + name + " help");
        this.name = name;
        this.setPermission("MurderMystery.op");
        this.setPermissionMessage("§c你没有权限");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (player.isOp()) {
                if (strings.length > 0) {
                    switch (strings[0]) {
                        case "设置出生点": case "setspawn":
                            murderMystery.roomSetSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage("§a默认出生点设置成功！");
                            return true;
                        case "添加随机出生点": case "addrandomspawn":
                            murderMystery.roomAddRandomSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage("§a随机出生点添加成功！");
                            return true;
                        case "添加金锭生成点": case "addgoldspawn":
                            murderMystery.roomAddGoldSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage("§a金锭生成点添加成功！");
                            return true;
                        case "设置金锭产出间隔":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    murderMystery.roomSetGoldSpawnTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                    commandSender.sendMessage("§a金锭产出间隔已设置为：" + Integer.valueOf(strings[1]));
                                }else {
                                    commandSender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                commandSender.sendMessage("§a查看帮助：/" + name + " help");
                            }
                            return true;
                        case "设置等待时间":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    murderMystery.roomSetWaitTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                    commandSender.sendMessage("§a等待时间已设置为：" + Integer.valueOf(strings[1]));
                                }else {
                                    commandSender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                commandSender.sendMessage("§a查看帮助：/" + name + " help");
                            }
                            return true;
                        case "设置游戏时间":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    if (Integer.parseInt(strings[1]) > 60) {
                                        murderMystery.roomSetGameTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                        commandSender.sendMessage("§a游戏时间已设置为：" + Integer.valueOf(strings[1]));
                                    } else {
                                        commandSender.sendMessage("§a游戏时间最小不能低于1分钟！");
                                    }
                                }else {
                                    commandSender.sendMessage("§a时间只能设置为正整数！");
                                }
                            }else {
                                commandSender.sendMessage("§a查看帮助：/" + name + " help");
                            }
                            return true;
                        case "reload": case "重载":
                            murderMystery.reLoadRooms();
                            commandSender.sendMessage("§a配置重载完成！请在后台查看信息！");
                            return true;
                        case "unload":
                            murderMystery.unloadRooms();
                            commandSender.sendMessage("§a已卸载所有房间！请在后台查看信息！");
                            return true;
                        default:
                            commandSender.sendMessage("§eMurderMystery--命令帮助");
                            commandSender.sendMessage("§a/" + name + " §e打开ui");
                            commandSender.sendMessage("§a/" + name + " 设置出生点 §e设置当前位置为游戏出生点");
                            commandSender.sendMessage("§a/" + name + " 添加金锭生成点 §e将当前位置设置为金锭生成点");
                            commandSender.sendMessage("§a/" + name + " 设置金锭产出间隔 数字 §e设置金锭生成间隔");
                            commandSender.sendMessage("§a/" + name + " 设置等待时间 数字 §e设置游戏人数足够后的等待时间");
                            commandSender.sendMessage("§a/" + name + " 设置游戏时间 数字 §e设置每轮游戏最长时间");
                            commandSender.sendMessage("§a/" + name + " reload §e重载所有房间");
                            commandSender.sendMessage("§a/" + name + " unload §e关闭所有房间,并卸载配置");
                            return true;
                    }
                }else {
                    GuiCreate.sendAdminMenu(player);
                    return true;
                }
            }else {
                commandSender.sendMessage("§c你没有权限！");
                return true;
            }
        }else {
            if(strings.length > 0 && strings[0].equals("reload")) {
                murderMystery.reLoadRooms();
                commandSender.sendMessage("§a配置重载完成！");
                return true;
            }else if(strings.length > 0 && strings[0].equals("unload")) {
                murderMystery.unloadRooms();
                commandSender.sendMessage("§a已卸载所有房间！");
                return true;
            }else {
                commandSender.sendMessage("§a请在游戏内输入！");
            }
            return true;
        }
    }

}
