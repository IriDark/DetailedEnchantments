package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import github.iri.anotherday.registries.blocks.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;

import java.util.function.*;

import static github.iri.anotherday.registries.ItemRegistry.BLOCK_ITEMS;

public class BlockRegistry{
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, AnotherDay.ID);
    public static RegistryObject<Block> oakHollow, spruceHollow;

    public static void load(IEventBus eventBus){
        oakHollow = registerBlock("oak_hollow", () -> new LogHollowBlock(Properties.of()));
        spruceHollow = registerBlock("spruce_hollow", () -> new LogHollowBlock(Properties.of()));
        BLOCK.register(eventBus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Supplier<? extends Item> item){
        RegistryObject<T> toReturn = BLOCK.register(name, block);
        BLOCK_ITEMS.register(name, item);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCK.register(name, block);
        BLOCK_ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }
}
