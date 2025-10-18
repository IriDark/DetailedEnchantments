package github.iri.anotherday.core.data;

import github.iri.anotherday.registries.*;
import net.minecraft.data.loot.*;
import net.minecraft.world.flag.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.*;

import java.util.*;
import java.util.function.*;

public class LootTableSubprovider extends BlockLootSubProvider{
    public final List<Block> blocks = new ArrayList<>();

    public LootTableSubprovider(){
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    protected void add(Block pBlock, Function<Block, LootTable.Builder> pFactory){
        this.add(pBlock, pFactory.apply(pBlock));
        blocks.add(pBlock);
    }

    protected void dropSelf(Block pBlock){
        this.dropOther(pBlock, pBlock);
        blocks.add(pBlock);
    }

    @Override
    protected Iterable<Block> getKnownBlocks(){
        return blocks;
    }

    @Override
    protected void generate(){
        this.dropSelf(BlockRegistry.oakHollow.get());
        this.dropSelf(BlockRegistry.spruceHollow.get());
    }
}