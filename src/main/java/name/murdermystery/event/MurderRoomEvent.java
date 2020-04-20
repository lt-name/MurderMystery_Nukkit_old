package name.murdermystery.event;

import cn.nukkit.event.Event;
import name.murdermystery.room.Room;

public abstract class MurderRoomEvent extends Event {

    protected Room room;

    public MurderRoomEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
