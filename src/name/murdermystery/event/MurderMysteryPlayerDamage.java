package name.murdermystery.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class MurderMysteryPlayerDamage extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private Player damage;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public MurderMysteryPlayerDamage(Player damage, Player player) {
        this.damage = damage;
        this.player = player;
    }

    public Player getDamage() {
        return this.damage;
    }

}
