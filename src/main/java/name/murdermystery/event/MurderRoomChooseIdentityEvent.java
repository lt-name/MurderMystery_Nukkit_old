package name.murdermystery.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import name.murdermystery.room.Room;

public class MurderRoomChooseIdentityEvent extends MurderRoomEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderRoomChooseIdentityEvent(Room room) {
        this.room = room;
    }

}
