package github.iri.anotherday.core.data;

import github.iri.anotherday.*;
import github.iri.anotherday.core.data.base.*;
import github.iri.anotherday.registries.*;
import net.minecraft.data.*;
import net.minecraftforge.common.data.*;

public class ItemModelGen extends CoreItemGen{
    public ItemModelGen(PackOutput output, ExistingFileHelper exFileHelper){
        super(output, AnotherDay.ID, exFileHelper);
    }

    @Override
    protected void registerModels(){
        spawnEgg(ItemRegistry.squirrelSpawnEgg.get());
    }
}
