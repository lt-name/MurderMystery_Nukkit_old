package name.killer.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.level.Level;
import name.killer.Killer;

public class GameLevel implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getPlayer().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getPlayer().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Level level = event.getEntity().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFrameDropItem(ItemFrameDropItemEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getItemFrame().getLevel();
        if (level != null && Killer.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

}
