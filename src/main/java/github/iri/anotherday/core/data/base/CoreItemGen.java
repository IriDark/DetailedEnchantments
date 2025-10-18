package github.iri.anotherday.core.data.base;

import net.minecraft.data.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.*;
import net.minecraftforge.registries.*;

import java.util.*;

public abstract class CoreItemGen extends ItemModelProvider{
    public String id;

    public CoreItemGen(PackOutput output, String modid, ExistingFileHelper exFileHelper){
        super(output, modid, exFileHelper);
        this.id = modid;
    }

    public ItemModelBuilder spawnEgg(Item item){
        return spawnEgg(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ItemModelBuilder spawnEgg(ResourceLocation item){
        return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }

}
