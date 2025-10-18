package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import github.iri.anotherday.registries.blocks.entities.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;

public class BlockEntitiesRegistry{
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AnotherDay.ID);
    public static final RegistryObject<BlockEntityType<HollowBlockEntity>> HOLLOW_BLOCK_ENTITIES = BLOCK_ENTITIES.register("hollow", () -> Builder.of(HollowBlockEntity::new, BlockRegistry.oakHollow.get(), BlockRegistry.spruceHollow.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}