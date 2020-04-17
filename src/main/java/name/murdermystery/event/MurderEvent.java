package name.murdermystery.event;

import cn.nukkit.event.player.PlayerEvent;
import name.murdermystery.room.Room;


public class MurderEvent extends PlayerEvent {

    protected Room room;

    public MurderEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
