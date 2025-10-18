package github.iri.anotherday.registries.blocks.entities;

import com.google.common.collect.*;
import github.iri.anotherday.registries.*;
import github.iri.anotherday.registries.blocks.*;
import github.iri.anotherday.registries.entities.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.sounds.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.gameevent.*;
import pro.komaru.tridot.common.registry.block.entity.*;
import pro.komaru.tridot.util.*;

import javax.annotation.*;
import java.util.*;

public class HollowBlockEntity extends BlockEntity implements TickableBlockEntity{
    public int maxSquirrels = 3;
    public int minReenterTick = 400;
    public int minOccupationTicks = 2400;

    private final List<HollowData> stored = Lists.newArrayList();
    private static final List<String> ignoredTags = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHomeTicks", "HomePos", "Passengers", "Leash", "UUID");

    public HollowBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegistry.HOLLOW_BLOCK_ENTITIES.get(), pPos, pBlockState);
    }

    /**
     * For block entities, ensures the chunk containing the block entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyHollow(null, this.level.getBlockState(this.getBlockPos()), ReleaseStatus.EMERGENCY);
        }

        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        } else {
            for(BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
                if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == maxSquirrels;
    }

    public void emptyHollow(@Nullable Player pPlayer, BlockState pState, ReleaseStatus pReleaseStatus) {
        List<Entity> list = this.releaseAllOccupants(pState, pReleaseStatus);
        if (pPlayer != null) {
            for(Entity entity : list) {
                if (entity instanceof Squirrel squirrel) {
                    if (pPlayer.position().distanceToSqr(entity.position()) <= 16.0D) {
                        squirrel.setCooldown(minReenterTick);
                    }
                }
            }
        }

    }

    private List<Entity> releaseAllOccupants(BlockState pState, ReleaseStatus pReleaseStatus) {
        List<Entity> list = Lists.newArrayList();
        this.stored.removeIf((p_272556_) -> releaseOccupant(this.level, this.worldPosition, pState, p_272556_, list, pReleaseStatus));
        if (!list.isEmpty()) {
            super.setChanged();
        }

        return list;
    }

    public void addOccupant(Entity pOccupant) {
        this.addOccupantWithPresetTicks(pOccupant, 0);
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public void addOccupantWithPresetTicks(Entity pOccupant, int pTicksInHome) {
        if (this.stored.size() < maxSquirrels) {
            pOccupant.stopRiding();
            pOccupant.ejectPassengers();
            CompoundTag compoundtag = new CompoundTag();
            pOccupant.save(compoundtag);
            this.storeSquirrels(compoundtag, pTicksInHome);
            if (this.level != null) {
                BlockPos blockpos = this.getBlockPos();
                this.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(pOccupant, this.getBlockState()));
            }

            pOccupant.discard();
            super.setChanged();
        }
    }

    public void storeSquirrels(CompoundTag pEntityData, int pTicksInHive) {
        this.stored.add(new HollowData(pEntityData, pTicksInHive,minOccupationTicks));
    }

    private static boolean releaseOccupant(Level pLevel, BlockPos pPos, BlockState pState, HollowData pData, @Nullable List<Entity> pStoredInHives, ReleaseStatus pReleaseStatus) {
        if (pLevel.isRaining() && pReleaseStatus != ReleaseStatus.EMERGENCY) {
            return false;
        } else {
            CompoundTag compoundtag = pData.entityData.copy();
            removeIgnoredTags(compoundtag);
            compoundtag.put("HomePos", NbtUtils.writeBlockPos(pPos));
            compoundtag.putBoolean("NoGravity", false);
            Direction direction = pState.getValue(LogHollowBlock.FACING);
            BlockPos blockpos = pPos.relative(direction);
            boolean flag = !pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos).isEmpty();
            if (flag && pReleaseStatus != ReleaseStatus.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityType.loadEntityRecursive(compoundtag, pLevel, (p_58740_) -> p_58740_);
                if (entity != null) {
                    if (!entity.getType().is(TagsRegistry.SQUIRREL_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof Squirrel squirrel) {
                            setReleaseData(pData.ticksInHollow, squirrel);
                            if (pStoredInHives != null) {
                                pStoredInHives.add(squirrel);
                            }

                            float f = entity.getBbWidth();
                            double d3 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
                            double d0 = (double)pPos.getX() + 0.5D + d3 * (double)direction.getStepX();
                            double d1 = (double)pPos.getY() + 0.5D - (double)(entity.getBbHeight() / 2.0F);
                            double d2 = (double)pPos.getZ() + 0.5D + d3 * (double)direction.getStepZ();
                            entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                        }

                        pLevel.playSound(null, pPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(entity, pLevel.getBlockState(pPos)));
                        return pLevel.addFreshEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    private static void setReleaseData(int pTimeInHive, Squirrel pSquirrel) {
        int i = pSquirrel.getAge();
        if (i < 0) {
            pSquirrel.setAge(Math.min(0, i + pTimeInHive));
        } else if (i > 0) {
            pSquirrel.setAge(Math.max(0, i - pTimeInHive));
        }

        pSquirrel.setInLoveTime(Math.max(0, pSquirrel.getInLoveTime() - pTimeInHive));
    }

    private void tickOccupants(Level pLevel, BlockPos pPos, BlockState pState, List<HollowData> pData) {
        boolean flag = false;
        HollowData data;
        for(Iterator<HollowData> iterator = pData.iterator(); iterator.hasNext(); ++data.ticksInHollow) {
            data = iterator.next();
            if (data.ticksInHollow > data.minOccupationTicks) {
                ReleaseStatus status = ReleaseStatus.RELEASED;
                if (releaseOccupant(pLevel, pPos, pState, data, null, status)) {
                    flag = true;
                    iterator.remove();
                }
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.stored.clear();
        ListTag listtag = pTag.getList("Squirrels", 10);
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            HollowBlockEntity.HollowData data = new HollowBlockEntity.HollowData(compoundtag.getCompound("EntityData"), compoundtag.getInt("TicksInHome"), compoundtag.getInt("MinOccupationTicks"));
            this.stored.add(data);
        }
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Squirrels", this.writeSquirrels());
    }

    public ListTag writeSquirrels() {
        ListTag listtag = new ListTag();
        for(HollowBlockEntity.HollowData data : this.stored) {
            CompoundTag compoundtag = data.entityData.copy();
            compoundtag.remove("UUID");
            CompoundTag compoundtag1 = new CompoundTag();
            compoundtag1.put("EntityData", compoundtag);
            compoundtag1.putInt("TicksInHome", data.ticksInHollow);
            compoundtag1.putInt("MinOccupationTicks", data.minOccupationTicks);
            listtag.add(compoundtag1);
        }

        return listtag;
    }

    static void removeIgnoredTags(CompoundTag pTag) {
        for(String s : ignoredTags) {
            pTag.remove(s);
        }
    }

    @Override
    public void tick(){
        tickOccupants(this.level, this.getBlockPos(), this.getBlockState(), this.stored);
        if (!this.stored.isEmpty() && Tmp.rnd.chance(0.05f)) {
            double d0 = (double)this.getBlockPos().getX() + 0.5D;
            double d1 = this.getBlockPos().getY();
            double d2 = (double)this.getBlockPos().getZ() + 0.5D;
            if(Tmp.rnd.chance(0.15f)){
                this.level.playSound(null, d0, d1, d2, SoundEvents.FOX_SLEEP, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                this.level.playSound(null, d0, d1, d2, SoundEvents.FOX_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public enum ReleaseStatus{
        RELEASED,
        EMERGENCY;
    }

    static class HollowData{
        final CompoundTag entityData;
        int ticksInHollow;
        final int minOccupationTicks;

        HollowData(CompoundTag pEntityData, int pTicksInHive, int pMinOccupationTicks) {
            HollowBlockEntity.removeIgnoredTags(pEntityData);
            this.entityData = pEntityData;
            this.ticksInHollow = pTicksInHive;
            this.minOccupationTicks = pMinOccupationTicks;
        }
    }
}
