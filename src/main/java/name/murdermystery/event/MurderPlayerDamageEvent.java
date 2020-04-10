package main.java.name.murdermystery.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import main.java.name.murdermystery.room.Room;

public class MurderPlayerDamageEvent extends MurderEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player damage;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderPlayerDamageEvent(Room room, Player damage, Player player) {
        this.room = room;
        this.damage = damage;
        this.player = player;
    }

    public Player getDamage() {
        return this.damage;
    }

}
