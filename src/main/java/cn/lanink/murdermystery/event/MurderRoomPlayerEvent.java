package cn.lanink.murdermystery.event;

import cn.lanink.murdermystery.room.Room;
import cn.nukkit.event.player.PlayerEvent;


public abstract class MurderRoomPlayerEvent extends PlayerEvent {

    protected Room room;

    public MurderRoomPlayerEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
