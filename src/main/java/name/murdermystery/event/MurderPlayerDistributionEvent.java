package main.java.name.murdermystery.event;

import cn.nukkit.event.HandlerList;
import main.java.name.murdermystery.room.Room;

public class MurderPlayerDistributionEvent extends MurderEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderPlayerDistributionEvent(Room room) {
        this.room = room;
    }

}
