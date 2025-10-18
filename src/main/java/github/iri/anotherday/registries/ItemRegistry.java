package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import net.minecraft.world.item.*;
import net.minecraftforge.common.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;
import pro.komaru.tridot.util.*;

import java.util.function.*;

public class ItemRegistry{
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AnotherDay.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AnotherDay.ID);

    public static RegistryObject<Item> squirrelSpawnEgg;

    public static void load(IEventBus eventBus){
        squirrelSpawnEgg = registerItem("squirrel_spawn_egg", () -> new ForgeSpawnEggItem(EntityTypeRegistry.SQUIRREL, Col.hexToDecimal("d14a2f"), Col.hexToDecimal("562b24"), new Item.Properties()));

        ITEMS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }

    private static RegistryObject<Item> registerItem(String name){
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> registerItem(String name, Rarity rarity){
        return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(rarity)));
    }

    private static RegistryObject<Item> registerItem(String name, Supplier<Item> item){
        return ITEMS.register(name, item);
    }
}