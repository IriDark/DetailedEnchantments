package github.iri.anotherday.core.data;

import github.iri.anotherday.*;
import github.iri.anotherday.core.data.base.*;
import github.iri.anotherday.registries.*;
import net.minecraft.data.*;
import net.minecraft.resources.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.*;

public class BlockStateGen extends CoreStateGen{

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper){
        super(output, AnotherDay.ID, exFileHelper);
    }

    public void hollowBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top){
        horizontalBlock(block, side, front, top);
        blockItem(block);
    }

    @Override
    protected void registerStatesAndModels(){
        hollowBlock(BlockRegistry.oakHollow.get(), mcLoc(ModelProvider.BLOCK_FOLDER + "/oak_log"), modLoc(ModelProvider.BLOCK_FOLDER + "/oak_hollow"), mcLoc(ModelProvider.BLOCK_FOLDER + "/oak_log_top"));
        hollowBlock(BlockRegistry.spruceHollow.get(), mcLoc(ModelProvider.BLOCK_FOLDER + "/spruce_log"), modLoc(ModelProvider.BLOCK_FOLDER + "/spruce_hollow"), mcLoc(ModelProvider.BLOCK_FOLDER + "/spruce_log_top"));
    }
}