package name.murdermystery.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import name.murdermystery.room.Room;

import java.util.Random;


public class Tools {

    /**
     * 重置玩家状态
     * @param player 玩家
     */
    public static void rePlayerState(Player player, boolean canSee) {
        if (player.getGamemode() != 0) {
            player.setGamemode(0);
        }
        if (player.isSprinting()) {
            player.setMovementSpeed(0.13F);
        }else {
            player.setMovementSpeed(0.1F);
        }
        player.setNameTagAlwaysVisible(canSee);
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
    }

    /**
     * 添加声音
     * @param room 房间
     * @param sound 声音
     */
    public static void addSound(Room room, Sound sound) {
        for (Player player : room.getPlayers().keySet()) {
            player.getLevel().addSound(new Vector3(player.x, player.y, player.z), sound);
        }
    }

    /**
     * 清理所有实体
     * @param level 世界
     */
    public static void cleanEntity(Level level, boolean clearAll) {
        if (clearAll) {
            for (Entity entity : level.getEntities()) {
                entity.close();
            }
        }else {
            cleanEntity(level);
        }
    }

    /**
     * 清理非玩家实体
     * @param level 世界
     */
    public static void cleanEntity(Level level) {
        for (Entity entity : level.getEntities()) {
            if (!(entity instanceof EntityHuman)) {
                if (!entity.getNameTag().equals("§e侦探之弓")) {
                    entity.close();
                }
            }
        }
    }

    /**
     * 放烟花
     * GitHub：https://github.com/SmallasWater/LuckDraw/blob/master/src/main/java/smallaswater/luckdraw/utils/Tools.java
     * @param player 玩家
     */
    public static void spawnFirework(Player player) {
        Level level = player.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor",new byte[]{
                (byte) DyeColor.values()[random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade",new byte[0]);
        ex.putBoolean("FireworkFlicker",random.nextBoolean());
        ex.putBoolean("FireworkTrail",random.nextBoolean());
        ex.putByte("FireworkType",ItemFirework.FireworkExplosion.ExplosionType.values()
                [random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks",(new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight",1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",player.x+0.5D))
                .add(new DoubleTag("",player.y+0.5D))
                .add(new DoubleTag("",player.z+0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0.0F))
                .add(new FloatTag("",0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(level.getChunk((int)player.x >> 4, (int)player.z >> 4), nbt);
        entity.spawnToAll();
    }

}
