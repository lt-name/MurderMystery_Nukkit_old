package main.java.name.murdermystery.ui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;


public class GuiCreate {

    public static final String PLUGIN_NAME = "§aMurderMystery";
    public static final int USER_MENU = 58894311;
    public static final int ADMIN_MENU = 58894312;
    public static final int ADMIN_TIME_MENU = 58894313;

    public static void sendUserMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        simple.addButton(new ElementButton("§e加入房间", new ElementButtonImageData("path", "textures/ui/switch_start_button")));
        simple.addButton(new ElementButton("§e退出房间", new ElementButtonImageData("path", "textures/ui/switch_select_button")));
        simple.addButton(new ElementButton("§e房间列表", new ElementButtonImageData("path", "textures/ui/servers")));
        player.showFormWindow(simple, USER_MENU);
    }

    public static void sendAdminMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        simple.addButton(new ElementButton("§e设置出生点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e添加金锭生成点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e设置时间参数", new ElementButtonImageData("path", "textures/ui/timer")));
        simple.addButton(new ElementButton("§e重载所有房间",  new ElementButtonImageData("path", "textures/ui/refresh_light")));
        simple.addButton(new ElementButton("§c卸载所有房间", new ElementButtonImageData("path", "textures/ui/redX1")));
        player.showFormWindow(simple, ADMIN_MENU);
    }

    public static void sendAdminTimeMenu(Player player) {
        FormWindowCustom custom = new FormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementInput("金锭产出间隔（秒）", "", "20"));
        custom.addElement(new ElementInput("等待时间（秒）", "", "60"));
        custom.addElement(new ElementInput("游戏时间（秒）", "", "300"));
        player.showFormWindow(custom, ADMIN_TIME_MENU);
    }

}
