package name.murdermystery.event;

import cn.nukkit.event.player.PlayerEvent;
import name.murdermystery.room.Room;


public abstract class MurderRoomPlayerEvent extends PlayerEvent {

    protected Room room;

    public MurderRoomPlayerEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
