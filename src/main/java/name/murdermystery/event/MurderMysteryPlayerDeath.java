package main.java.name.murdermystery.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import main.java.name.murdermystery.room.Room;

public class MurderMysteryPlayerDeath extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderMysteryPlayerDeath(Room room, Player player) {
        this.room = room;
        this.player = player;
    }

    public Room getRoom() {
        return this.room;
    }

}
