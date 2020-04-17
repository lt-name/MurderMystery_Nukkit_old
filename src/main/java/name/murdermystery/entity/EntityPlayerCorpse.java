package name.murdermystery.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;

/**
 * 玩家尸体
 * @author 若水
 */
public class EntityPlayerCorpse extends EntityHuman {

    public EntityPlayerCorpse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setNameTagVisible(false);
        this.setNameTagAlwaysVisible(false);
        this.setNameTag(" ");
        this.setDataProperty(new FloatEntityData(38, this.namedTag.getFloat("scale")));
    }

    @Override
    public void spawnTo(Player player) {
        if(!this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});
            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float)this.x;
            pk.y = (float)this.y;
            pk.z = (float)this.z;
            pk.speedX = (float)this.motionX;
            pk.speedY = (float)this.motionY;
            pk.speedZ = (float)this.motionZ;
            pk.yaw = (float)this.yaw;
            pk.pitch = (float)this.pitch;
            pk.item = Item.get(0);
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);
            setSkin(this.skin);
            this.inventory.setItemInHand(Item.get(0));
            super.spawnTo(player);
        }
    }

}
