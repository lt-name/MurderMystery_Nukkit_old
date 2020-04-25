package cn.lanink.murdermystery.event;

import cn.lanink.murdermystery.room.Room;
import cn.nukkit.event.Event;

public abstract class MurderRoomEvent extends Event {

    protected Room room;

    public MurderRoomEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
