package cn.lanink.murdermystery.addons.uishop;

import cn.lanink.murdermystery.MurderMystery;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;

import java.util.ArrayList;

public class UiShop {

    public UiShop(MurderMystery murderMystery) {
        murderMystery.saveResource("Resources/Addons/UiShop/config.yml", "/Addons/UiShop/config.yml", false);
        ArrayList<String> items = (ArrayList<String>) new Config(
                murderMystery.getDataFolder() + "/Addons/UiShop/config.yml", 2).getStringList("items");
        Server.getInstance().getPluginManager().registerEvents(new UiShopListener(items), murderMystery);
    }



}
