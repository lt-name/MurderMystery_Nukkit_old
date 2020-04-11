package main.java.name.murdermystery.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import main.java.name.murdermystery.room.Room;

public class MurderPlayerCorpseSpawnEvent extends MurderEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderPlayerCorpseSpawnEvent(Room room, Player player) {
        this.room = room;
        this.player = player;
    }

}
