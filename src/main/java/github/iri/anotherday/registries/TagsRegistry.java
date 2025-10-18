package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.poi.*;
import net.minecraft.world.level.block.*;

public class TagsRegistry{
    public static final TagKey<PoiType> SQUIRREL_HOME = poi(AnotherDay.loc("squirrel_home"));
    public static final TagKey<Block> SQUIRREL_HOMES = block(AnotherDay.loc("squirrel_homes"));
    public static final TagKey<Block> SQUIRREL_CLIMBABLE = block(AnotherDay.loc("squirrel_climbable"));
    public static final TagKey<EntityType<?>> SQUIRREL_INHABITORS = type(AnotherDay.loc("squirrel_inhabitors"));

    public static TagKey<Block> block(final ResourceLocation name){
        return TagKey.create(Registries.BLOCK, name);
    }

    public static TagKey<EntityType<?>> type(final ResourceLocation name){
        return TagKey.create(Registries.ENTITY_TYPE, name);
    }


    private static TagKey<PoiType> poi(final ResourceLocation name) {
        return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, name);
    }
}
