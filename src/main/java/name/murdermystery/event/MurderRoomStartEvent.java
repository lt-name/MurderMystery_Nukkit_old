package name.murdermystery.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import name.murdermystery.room.Room;

public class MurderRoomStartEvent extends MurderEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderRoomStartEvent(Room room) {
        this.room = room;
    }

}
