package github.iri.anotherday.core.data.base;

import github.iri.anotherday.core.data.*;
import net.minecraft.data.*;
import net.minecraft.data.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;

import java.util.*;

public class LootTableGen{

    public static LootTableProvider create(PackOutput output){
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableSubprovider::new, LootContextParamSets.BLOCK)
        ));
    }
}