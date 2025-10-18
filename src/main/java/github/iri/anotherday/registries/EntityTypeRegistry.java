package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import github.iri.anotherday.registries.entities.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;

public class EntityTypeRegistry{
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AnotherDay.ID);

    public static final RegistryObject<EntityType<Squirrel>> SQUIRREL = register("squirrel", EntityType.Builder.of(Squirrel::new, MobCategory.AMBIENT).sized(0.75f, 0.75f).clientTrackingRange(8));

    public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder){
        return ENTITY_TYPES.register(name, () -> builder.build(new ResourceLocation(AnotherDay.ID, name).toString()));
    }

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
