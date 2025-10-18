package github.iri.anotherday.registries;

import com.google.common.collect.*;
import github.iri.anotherday.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.ai.village.poi.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;

import java.util.*;

public class MiscRegistry{
    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, AnotherDay.ID);
    public static final RegistryObject<PoiType> OAK_HOLLOW = POI.register("oak_hollow", () -> type(BlockRegistry.oakHollow.get(), 0, 1));
    public static final RegistryObject<PoiType> SPRUCE_HOLLOW = POI.register("spruce_hollow", () -> type(BlockRegistry.spruceHollow.get(), 0, 1));

    private static Set<BlockState> getBlockStates(Block pBlock){
        return ImmutableSet.copyOf(pBlock.getStateDefinition().getPossibleStates());
    }

    private static PoiType type(Block block, int pMaxTickets, int pValidRange){
        return new PoiType(getBlockStates(block), pMaxTickets, pValidRange);
    }

    private static PoiType type(Set<BlockState> pMatchingStates, int pMaxTickets, int pValidRange){
        return new PoiType(pMatchingStates, pMaxTickets, pValidRange);
    }

    public static void init(IEventBus eventBus){
        POI.register(eventBus);
    }
}
