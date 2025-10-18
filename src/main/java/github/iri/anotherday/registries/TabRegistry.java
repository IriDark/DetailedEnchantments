package github.iri.anotherday.registries;

import github.iri.anotherday.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;
import net.minecraftforge.event.*;
import net.minecraftforge.registries.*;

public class TabRegistry{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AnotherDay.ID);
    public static final RegistryObject<CreativeModeTab> ANOTHERDAY_TAB = CREATIVE_MODE_TABS.register("anotherday_tab",
    () -> CreativeModeTab.builder().icon(() -> new ItemStack(BlockRegistry.oakHollow.get()))
    .hideTitle()
    .title(Component.translatable("itemGroup.anotherday_tab")).build());


    public static void addCreative(BuildCreativeModeTabContentsEvent event){
        var tabKey = event.getTabKey();
        if (tabKey == TabRegistry.ANOTHERDAY_TAB.getKey()) {
            for(var entry : ItemRegistry.BLOCK_ITEMS.getEntries()){
                event.accept(entry.get());
            }

            for(var entry : ItemRegistry.ITEMS.getEntries()){
                event.accept(entry.get());
            }
        }
    }
}
