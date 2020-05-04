package cn.lanink.murdermystery.addons;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.addons.uishop.UiShop;
import cn.nukkit.utils.Config;

public class Addons {

    public Addons(MurderMystery murderMystery) {
        murderMystery.saveResource("Resources/Addons/config.yml", "/Addons/config.yml", false);
        Config config = new Config(murderMystery.getDataFolder() + "/Addons/config.yml", 2);
        if (config.getBoolean("UiShop", false)) {
            new UiShop(murderMystery);
            murderMystery.getLogger().info("§aUiShop 扩展已加载！");
        }

    }

}
