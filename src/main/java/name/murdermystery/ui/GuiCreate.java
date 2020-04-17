package name.murdermystery.ui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import name.murdermystery.MurderMystery;
import name.murdermystery.room.Room;

import java.util.Map;


public class GuiCreate {

    public static final String PLUGIN_NAME = "§l§7[§1M§2u§3r§4d§5e§6r§aM§cy§bs§dt§9e§6r§2y§7]";
    public static final int USER_MENU = 58894311;
    public static final int ADMIN_MENU = 58894312;
    public static final int ADMIN_TIME_MENU = 58894313;
    public static final int ROOM_LIST_MENU = 58894314;
    public static final int ROOM_JOIN_OK = 58894315;

    /**
     * 显示用户菜单
     * @param player 玩家
     */
    public static void sendUserMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        simple.addButton(new ElementButton("§e随机加入房间", new ElementButtonImageData("path", "textures/ui/switch_start_button")));
        simple.addButton(new ElementButton("§e退出当前房间", new ElementButtonImageData("path", "textures/ui/switch_select_button")));
        simple.addButton(new ElementButton("§e查看房间列表", new ElementButtonImageData("path", "textures/ui/servers")));
        player.showFormWindow(simple, USER_MENU);
    }

    /**
     * 显示管理菜单
     * @param player 玩家
     */
    public static void sendAdminMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "当前设置地图：" + player.getLevel().getName());
        simple.addButton(new ElementButton("§e设置出生点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e添加金锭生成点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e设置时间参数", new ElementButtonImageData("path", "textures/ui/timer")));
        simple.addButton(new ElementButton("§e重载所有房间",  new ElementButtonImageData("path", "textures/ui/refresh_light")));
        simple.addButton(new ElementButton("§c卸载所有房间", new ElementButtonImageData("path", "textures/ui/redX1")));
        player.showFormWindow(simple, ADMIN_MENU);
    }

    /**
     * 显示设置时间菜单
     * @param player 玩家
     */
    public static void sendAdminTimeMenu(Player player) {
        FormWindowCustom custom = new FormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementInput("金锭产出间隔（秒）", "", "20"));
        custom.addElement(new ElementInput("等待时间（秒）", "", "60"));
        custom.addElement(new ElementInput("游戏时间（秒）", "", "300"));
        player.showFormWindow(custom, ADMIN_TIME_MENU);
    }

    /**
     * 显示房间列表菜单
     * @param player 玩家
     */
    public static void sendRoomListMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        for (Map.Entry<String, Room> entry : MurderMystery.getInstance().getRooms().entrySet()) {
            simple.addButton(new ElementButton("§e" + entry.getKey(), new ElementButtonImageData("path", "textures/ui/switch_start_button")));
        }
        simple.addButton(new ElementButton("§c返回", new ElementButtonImageData("path", "textures/ui/cancel")));
        player.showFormWindow(simple, ROOM_LIST_MENU);
    }

    /**
     * 加入房间确认(自选)
     * @param player 玩家
     */
    public static void sendRoomJoinOkMenu(Player player, String roomName) {
        if (MurderMystery.getInstance().getRooms().containsKey(roomName.replace("§e", "").trim())) {
            Room room = MurderMystery.getInstance().getRooms().get(roomName.replace("§e", "").trim());
            if (room.getMode() == 2 || room.getMode() == 3) {
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§l§c房间: " + roomName + "§c 正在游戏中！", "§c返回", "§c返回");
                player.showFormWindow(modal, ROOM_JOIN_OK);
            }else if (room.getPlayers().size() > 15){
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§l§c房间: " + roomName + "§c 已满人！", "§c返回", "§c返回");
                player.showFormWindow(modal, ROOM_JOIN_OK);
            }else {
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§l§a确认要加入房间: \"" + roomName + "\" §l§a？", "§a加入", "§c返回");
                player.showFormWindow(modal, ROOM_JOIN_OK);
            }
        }else {
            FormWindowModal modal = new FormWindowModal(
                    PLUGIN_NAME, "§l§c房间: " + roomName + "§c 不存在！", "§c返回", "§c返回");
            player.showFormWindow(modal, ROOM_JOIN_OK);
        }
    }

}
