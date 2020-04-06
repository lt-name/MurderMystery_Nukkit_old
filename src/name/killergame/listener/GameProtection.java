package name.killergame.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.StartBrewEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.level.Level;
import name.killergame.KillerGame;

/**
 * 游戏保护
 * 禁止除游戏规则外的其他事件
 */
public class GameProtection implements Listener {

    /**
     * 物品合成事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 开始酿造事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onStartBrew(StartBrewEvent event) {
        Level level = event.getBrewingStand().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 方块放置事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getPlayer().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 方块破坏事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getPlayer().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 实体爆炸事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Level level = event.getEntity().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 物品展示框丢出事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onFrameDropItem(ItemFrameDropItemEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.isOp()) {
            return;
        }
        Level level = event.getItemFrame().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 饥饿值变化事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 丢出物品事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event) {
        Level level = event.getPlayer().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 伤害事件
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        Level level = event.getEntity().getLevel();
        if (level != null && KillerGame.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

}