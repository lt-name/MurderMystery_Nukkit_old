package name.murdermystery.ui;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import name.murdermystery.MurderMystery;

public class GuiListener implements Listener {

    /**
     * 玩家操作ui事件
     * 直接执行现有命令，减小代码重复量，也便于维护
     * @param event 事件
     */
    @EventHandler
    public void onPlayerFormResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getWindow() == null || event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple simple = (FormWindowSimple) event.getWindow();
            if (event.getFormID() == GuiCreate.USER_MENU) {
                switch (simple.getResponse().getClickedButtonId()) {
                    case 0:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "killer join");
                        break;
                    case 1:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "killer quit");
                        break;
                    case 2:
                        GuiCreate.sendRoomListMenu(player);
                        break;
                }
            }else if (event.getFormID() == GuiCreate.ROOM_LIST_MENU) {
                if (simple.getResponse().getClickedButton().getText().equals("§c返回")) {
                    GuiCreate.sendUserMenu(player);
                }else {
                    GuiCreate.sendRoomJoinOkMenu(player, simple.getResponse().getClickedButton().getText());
                }
            }else if (event.getFormID() == GuiCreate.ADMIN_MENU) {
                switch (simple.getResponse().getClickedButtonId()) {
                    case 0:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置出生点");
                        break;
                    case 1:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 添加随机出生点");
                        break;
                    case 2:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 添加金锭生成点");
                        break;
                    case 3:
                        GuiCreate.sendAdminTimeMenu(player);
                        break;
                    case 4:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin reload");
                        break;
                    case 5:
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin unload");
                        break;
                }
            }
        }else if (event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom custom = (FormWindowCustom) event.getWindow();
            if (event.getFormID() == GuiCreate.ADMIN_TIME_MENU) {
                if (custom.getResponse().getInputResponse(0).matches("[0-9]*")) {
                    MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置金锭产出间隔 " + custom.getResponse().getInputResponse(0));
                }else {
                    player.sendMessage("§a金锭产出间隔只能设置为正整数！");
                }
                if (custom.getResponse().getInputResponse(1).matches("[0-9]*")) {
                    MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置等待时间 " + custom.getResponse().getInputResponse(1));
                }else {
                    player.sendMessage("§a等待时间只能设置为正整数！");
                }
                if (custom.getResponse().getInputResponse(2).matches("[0-9]*")) {
                    MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置游戏时间 " + custom.getResponse().getInputResponse(2));
                }else {
                    player.sendMessage("§a游戏时间只能设置为正整数！");
                }
            }
        }else if (event.getWindow() instanceof FormWindowModal) {
            FormWindowModal modal = (FormWindowModal) event.getWindow();
            if (event.getFormID() == GuiCreate.ROOM_JOIN_OK) {
                if (modal.getResponse().getClickedButtonId() == 0 && !modal.getButton1().equals("§c返回")) {
                    String[] s = modal.getContent().split("\"");
                    MurderMystery.getInstance().getServer().dispatchCommand(
                            player, "killer join " + s[1].replace("§e", "").trim());
                }else {
                    GuiCreate.sendRoomListMenu(player);
                }
            }
        }
    }

}