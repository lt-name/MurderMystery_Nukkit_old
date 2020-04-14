package main.java.name.murdermystery.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.java.name.murdermystery.MurderMystery;
import main.java.name.murdermystery.ui.GuiCreate;

import java.lang.reflect.Type;
import java.util.Map;

public class GuiListener implements Listener {

    /**
     * 玩家操作ui事件
     * 直接执行现有命令，减小代码重复量，也便于维护
     * @param event 事件
     */
    @EventHandler
    public void onPlayerFormResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getWindow() == null) {
            return;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        if (event.getFormID() == GuiCreate.USER_MENU) {
            Map<String, Object> data = gson.fromJson(event.getWindow().getJSONData(), type);
            if (data.containsKey("response")) {
                Map data1 = (Map) data.get("response");
                if (data1.containsKey("clickedButtonId")) {
                    switch (data1.get("clickedButtonId").toString()) {
                        case "0.0":
                            MurderMystery.getInstance().getServer().dispatchCommand(player, "killer join");
                            break;
                        case "1.0":
                            MurderMystery.getInstance().getServer().dispatchCommand(player, "killer quit");
                            break;
                        case "2.0":
                            MurderMystery.getInstance().getServer().dispatchCommand(player, "killer list");
                            break;
                    }
                }
            }
        }else if (event.getFormID() == GuiCreate.ADMIN_MENU) {
            Map<String, Object> data = gson.fromJson(event.getWindow().getJSONData(), type);
            if (data.containsKey("response")) {
                Map data1 = (Map) data.get("response");
                switch (data1.get("clickedButtonId").toString()) {
                    case "0.0":
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置出生点");
                        break;
                    case "1.0":
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 添加金锭生成点");
                        break;
                    case "2.0":
                        GuiCreate.sendAdminTimeMenu(player);
                        break;
                    case "3.0":
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin reload");
                        break;
                    case "4.0":
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin unload");
                        break;
                }
            }
        }else if (event.getFormID() == GuiCreate.ADMIN_TIME_MENU) {
            Map<String, Object> data = gson.fromJson(event.getWindow().getJSONData(), type);
            if (data.containsKey("response")) {
                Map data1 = (Map) data.get("response");
                if (data1.containsKey("inputResponses")) {
                    Map data2 = (Map) data1.get("inputResponses");
                    if (data2.containsKey("0")) {
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置金锭产出间隔 " + data2.get("0"));
                    }
                    if (data2.containsKey("1")) {
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置等待时间 " + data2.get("1"));
                    }
                    if (data2.containsKey("2")) {
                        MurderMystery.getInstance().getServer().dispatchCommand(player, "kadmin 设置游戏时间 " + data2.get("2"));
                    }

                }
            }
        }
    }

}
