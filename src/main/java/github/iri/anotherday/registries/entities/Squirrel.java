package github.iri.anotherday.registries.entities;

import com.google.common.collect.*;
import github.iri.anotherday.*;
import github.iri.anotherday.core.ai.*;
import github.iri.anotherday.registries.*;
import github.iri.anotherday.registries.blocks.entities.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.entity.ai.village.poi.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.*;

public class Squirrel extends Animal{
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    private final int homeSearchDistance = 20;
    private final int pathfindToHomeDistance = 24;
    private int cooldownBeforeLocatingNewHome;
    private int stayOutOfHomeCountdown;

    @Nullable
    private BlockPos homePos;
    Squirrel.GoToHomeGoal goToHomeGoal;

    public Squirrel(EntityType<? extends Animal> pEntityType, Level pLevel){
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    /**
     * Plays living's sound at its position
     */
    public void playAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent == SoundEvents.FOX_SCREECH) {
            this.playSound(soundevent, 2.0F, this.getVoicePitch());
        } else {
            super.playAmbientSound();
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound(){
        return SoundEvents.FOX_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.FOX_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.FOLLOW_RANGE, 32);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(1, new EnterHomeGoal());
        this.goalSelector.addGoal(2, new LocateNewHomeGoal());
        this.goalSelector.addGoal(1, new SquirrelWanderGoal());
        this.goToHomeGoal = new GoToHomeGoal();
        this.goalSelector.addGoal(5, this.goToHomeGoal);

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));

        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Player.class, (p) -> !p.isSteppingCarefully(), 8, 1, 1, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Wolf.class, 16, 1.1, 2));
        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Fox.class, 16, 1.1, 2));
    }

    public boolean wantsHome() {
        if (this.stayOutOfHomeCountdown <= 0) {
            boolean flag = this.level().isRaining() || this.level().isNight();
            return flag && !this.isHomeNearFire();
        } else {
            return false;
        }
    }

    private boolean isHomeNearFire() {
        if (this.homePos == null) {
            return false;
        } else {
            BlockEntity blockentity = this.level().getBlockEntity(this.homePos);
            return blockentity instanceof HollowBlockEntity hollow && hollow.isFireNearby();
        }
    }

    private boolean doesHomeHaveSpace(BlockPos pHivePos) {
        BlockEntity blockentity = this.level().getBlockEntity(pHivePos);
        if (blockentity instanceof HollowBlockEntity home) {
            return !home.isFull();
        } else {
            return false;
        }
    }

    @VisibleForDebug
    public boolean hasHome() {
        return this.homePos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPos getHomePos() {
        return this.homePos;
    }

    public void setCooldown(int pStayOutOfHiveCountdown) {
        this.stayOutOfHomeCountdown = pStayOutOfHiveCountdown;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.hasHome()) {
            pCompound.put("HomePos", NbtUtils.writeBlockPos(this.getHomePos()));
        }

        pCompound.putInt("CannotEnterHomeTicks", this.stayOutOfHomeCountdown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.homePos = null;
        if (pCompound.contains("HomePos")) {
            this.homePos = NbtUtils.readBlockPos(pCompound.getCompound("HomePos"));
        }

        this.stayOutOfHomeCountdown = pCompound.getInt("CannotEnterHomeTicks");
    }

    @Nullable
    public Animal getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityTypeRegistry.SQUIRREL.get().create(pLevel);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isFood(ItemStack pStack) {
        return FOOD_ITEMS.test(pStack);
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return this.isBaby() ? pSize.height * 0.85F : pSize.height * 0.92F;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel){
        var nav = new SquirrelNavigation(this, pLevel);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(false);
        nav.setCanPassDoors(true);
        return nav;
    }

    /**
     * Called every tick so the entity can update its state as required. For example, zombies and skeletons use this to
     * react to sunlight and start to burn.
     */
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.stayOutOfHomeCountdown > 0) {
                --this.stayOutOfHomeCountdown;
            }

            if (this.cooldownBeforeLocatingNewHome > 0) {
                --this.cooldownBeforeLocatingNewHome;
            }

            if (this.tickCount % 20 == 0 && !this.isHomeValid()) {
                this.homePos = null;
            }
        }
    }

    void pathfindRandomlyTowards(BlockPos pPos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(pPos);
        Vec3 vec31 = LandRandomPos.getPosTowards(this, 16, 16, vec3);
        if (vec31 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec31.x, vec31.y, vec31.z, 1.0D);
        }
    }

    public boolean isHomeValid() {
        if (!this.hasHome()) {
            return false;
        } else if (this.isTooFarAway(this.homePos)) {
            return false;
        } else {
            BlockEntity blockentity = this.level().getBlockEntity(this.homePos);
            return blockentity instanceof HollowBlockEntity;
        }
    }

    public boolean isTooFarAway(BlockPos pPos) {
        return !this.closerThan(pPos, 32);
    }

    boolean closerThan(BlockPos pPos, int pDistance) {
        return pPos.closerThan(this.blockPosition(), pDistance);
    }

    /**
     * Returns {@code true} if this entity should move as if it were on a ladder (either because it's actually on a
     * ladder, or for AI reasons)
     */
    public boolean onClimbable() {
        return ((SquirrelNavigation) this.getNavigation()).isAgainstClimbableWall() || super.onClimbable();
    }

    class EnterHomeGoal extends Goal {
        public boolean canUse() {
            if (Squirrel.this.hasHome() && Squirrel.this.wantsHome() && Squirrel.this.homePos.closerToCenterThan(Squirrel.this.position(), 2.0D)) {
                BlockEntity blockentity = Squirrel.this.level().getBlockEntity(Squirrel.this.homePos);
                if (blockentity instanceof HollowBlockEntity home) {
                    if (!home.isFull()) {
                        return true;
                    }

                    Squirrel.this.homePos = null;
                }
            }

            return false;
        }

        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            BlockEntity blockentity = Squirrel.this.level().getBlockEntity(Squirrel.this.homePos);
            if (blockentity instanceof HollowBlockEntity home) {
                home.addOccupant(Squirrel.this);
            }

        }
    }

    @VisibleForDebug
    public class GoToHomeGoal extends Goal {
        int travellingTicks = Squirrel.this.level().random.nextInt(10);
        final List<BlockPos> blacklistedTargets = Lists.newArrayList();
        @Nullable
        private Path lastPath;
        private int ticksStuck;

        GoToHomeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return Squirrel.this.homePos != null && !Squirrel.this.hasRestriction() && Squirrel.this.wantsHome() && !this.hasReachedTarget(Squirrel.this.homePos) && Squirrel.this.level().getBlockState(Squirrel.this.homePos).is(TagsRegistry.SQUIRREL_HOMES);
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            super.start();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            Squirrel.this.navigation.stop();
            Squirrel.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        public void tick() {
            if (Squirrel.this.homePos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > this.adjustedTickDelay(600)) {
                    this.dropAndBlacklistHome();
                } else if (!Squirrel.this.navigation.isInProgress()) {
                    if (!Squirrel.this.closerThan(Squirrel.this.homePos, Squirrel.this.pathfindToHomeDistance)) {
                        if (Squirrel.this.isTooFarAway(Squirrel.this.homePos)) {
                            this.dropHome();
                        } else {
                            Squirrel.this.pathfindRandomlyTowards(Squirrel.this.homePos);
                        }
                    } else {
                        boolean flag = this.pathfindDirectlyTowards(Squirrel.this.homePos);
                        if (!flag) {
                            this.dropAndBlacklistHome();
                        } else if (this.lastPath != null && Squirrel.this.navigation.getPath().sameAs(this.lastPath)) {
                            ++this.ticksStuck;
                            if (this.ticksStuck > 60) {
                                this.dropHome();
                                this.ticksStuck = 0;
                            }
                        } else {
                            this.lastPath = Squirrel.this.navigation.getPath();
                        }

                    }
                }
            }
        }

        private boolean pathfindDirectlyTowards(BlockPos pPos) {
            Squirrel.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            Squirrel.this.navigation.moveTo(pPos.getX(), pPos.getY(), pPos.getZ(), 1.0D);
            return Squirrel.this.navigation.getPath() != null && Squirrel.this.navigation.getPath().canReach();
        }

        boolean isTargetBlacklisted(BlockPos pPos) {
            return this.blacklistedTargets.contains(pPos);
        }

        private void blacklistTarget(BlockPos pPos) {
            this.blacklistedTargets.add(pPos);
            while(this.blacklistedTargets.size() > 3) {
                this.blacklistedTargets.remove(0);
            }
        }

        void clearBlacklist() {
            this.blacklistedTargets.clear();
        }

        private void dropAndBlacklistHome() {
            if (Squirrel.this.homePos != null) {
                this.blacklistTarget(Squirrel.this.homePos);
            }

            this.dropHome();
        }

        private void dropHome() {
            Squirrel.this.homePos = null;
            Squirrel.this.cooldownBeforeLocatingNewHome = 200;
        }

        private boolean hasReachedTarget(BlockPos pPos) {
            if (Squirrel.this.closerThan(pPos, 2)) {
                return true;
            } else {
                Path path = Squirrel.this.navigation.getPath();
                return path != null && path.getTarget().equals(pPos) && path.canReach() && path.isDone();
            }
        }
    }

    class LocateNewHomeGoal extends Goal {
        public boolean canUse() {
            return Squirrel.this.cooldownBeforeLocatingNewHome == 0 && !Squirrel.this.hasHome() && Squirrel.this.wantsHome();
        }

        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            Squirrel.this.cooldownBeforeLocatingNewHome = 200;
            List<BlockPos> list = this.findNearbyHome();
            AnotherDay.LOGGER.debug("list {}", list);

            if (!list.isEmpty()) {
                for(BlockPos blockpos : list) {
                    if (!Squirrel.this.goToHomeGoal.isTargetBlacklisted(blockpos)) {
                        Squirrel.this.homePos = blockpos;
                        return;
                    }
                }

                Squirrel.this.goToHomeGoal.clearBlacklist();
                Squirrel.this.homePos = list.get(0);
            }
        }

        private List<BlockPos> findNearbyHome() {
            BlockPos blockpos = Squirrel.this.blockPosition();
            PoiManager poimanager = ((ServerLevel)Squirrel.this.level()).getPoiManager();
            Stream<PoiRecord> stream = poimanager.getInRange((p) -> p.is(TagsRegistry.SQUIRREL_HOME), blockpos, Squirrel.this.homeSearchDistance, PoiManager.Occupancy.ANY);
            return stream.map(PoiRecord::getPos).filter(Squirrel.this::doesHomeHaveSpace).sorted(Comparator.comparingDouble((p) -> p.distSqr(blockpos))).collect(Collectors.toList());
        }
    }

    class SquirrelWanderGoal extends Goal {
        SquirrelWanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return Squirrel.this.navigation.isDone() && Squirrel.this.random.nextInt(10) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return Squirrel.this.navigation.isInProgress();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                Squirrel.this.navigation.moveTo(Squirrel.this.navigation.createPath(BlockPos.containing(vec3), 0), 1.0D);
            }

        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3;
            if (Squirrel.this.isHomeValid() && !Squirrel.this.closerThan(Squirrel.this.homePos, Squirrel.this.homeSearchDistance)) {
                Vec3 vec31 = Vec3.atCenterOf(Squirrel.this.homePos);
                vec3 = vec31.subtract(Squirrel.this.position()).normalize();
            } else {
                vec3 = Squirrel.this.getViewVector(0.0F);
            }

            Vec3 vec32 = HoverRandomPos.getPos(Squirrel.this, Squirrel.this.homeSearchDistance, Squirrel.this.homeSearchDistance, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec32 != null ? vec32 : DefaultRandomPos.getPos(Squirrel.this, Squirrel.this.homeSearchDistance, Squirrel.this.homeSearchDistance);
        }
    }
}