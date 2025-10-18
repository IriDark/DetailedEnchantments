package github.iri.anotherday.core.ai;

import github.iri.anotherday.registries.*;
import net.minecraft.core.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

import javax.annotation.*;

public class SquirrelNavigation extends GroundPathNavigation{

   @Nullable
   private BlockPos pathToPosition;
   public SquirrelNavigation(Mob pMob, Level pLevel) {
      super(pMob, pLevel);
   }

    public Path createPath(BlockPos pPos, int pAccuracy) {
        this.pathToPosition = pPos;
        return super.createPath(pPos, pAccuracy);
    }

    public Path createPath(Entity pEntity, int pAccuracy) {
        this.pathToPosition = pEntity.blockPosition();
        return super.createPath(pEntity, pAccuracy);
    }

    public boolean moveTo(Entity pEntity, double pSpeed) {
        Path path = this.createPath(pEntity, 0);
        if (path != null) {
            return this.moveTo(path, pSpeed);
        } else {
            this.pathToPosition = pEntity.blockPosition();
            this.speedModifier = pSpeed;
            return true;
        }
    }


   @Override
   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else {
         if (this.pathToPosition != null) {
            if (!this.pathToPosition.closerToCenterThan(this.mob.position(), Math.max(this.mob.getBbWidth(), 1.0D)) &&
                (!(this.mob.getY() > (double)this.pathToPosition.getY()) || !(BlockPos.containing(this.pathToPosition.getX(), this.mob.getY(), this.pathToPosition.getZ())).closerToCenterThan(this.mob.position(), Math.max(this.mob.getBbWidth(), 1.0D)))) {
                if (isAgainstClimbableWall()) {
                    this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speedModifier);
                }

            } else {
               this.pathToPosition = null;
            }
         }
      }
   }

   public boolean isAgainstClimbableWall() {
        Vec3 delta = this.mob.getDeltaMovement();
        Vec3 horizontalDelta = new Vec3(delta.x, 0, delta.z);
        if (horizontalDelta.lengthSqr() == 0) {
            Vec3 facing = Vec3.atLowerCornerOf(this.mob.getDirection().getNormal());
            horizontalDelta = facing.scale(0.1); // Small nudge in the facing direction
        }

        AABB currentAABB = this.mob.getBoundingBox();
        AABB futureAABB = currentAABB.move(horizontalDelta);

        for (BlockPos pos : BlockPos.betweenClosed((int) Math.floor(futureAABB.minX), (int) Math.floor(futureAABB.minY), (int) Math.floor(futureAABB.minZ), (int) Math.ceil(futureAABB.maxX), (int) Math.ceil(futureAABB.maxY), (int) Math.ceil(futureAABB.maxZ))) {
            BlockState blockState = this.level.getBlockState(pos);
            VoxelShape blockShape = blockState.getCollisionShape(this.level, pos);

            if (blockShape.isEmpty()) continue;

            if (Shapes.joinIsNotEmpty(blockShape.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(futureAABB), BooleanOp.AND)) {
                if (blockState.is(TagsRegistry.SQUIRREL_CLIMBABLE)) {
                    return true;
                }
            }
        }

        return false;
    }
}