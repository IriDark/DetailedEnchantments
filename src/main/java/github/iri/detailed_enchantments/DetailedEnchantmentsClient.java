package github.iri.detailed_enchantments;

import github.iri.detailed_enchantments.core.components.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;

public class DetailedEnchantmentsClient{

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryEvents{

        @SubscribeEvent
        public static void registerComponents(RegisterClientTooltipComponentFactoriesEvent e){
            e.register(EnchantmentComponent.class, EnchantmentClientComponent::new);
            e.register(ItemEnchantmentComponent.class, c -> ItemEnchantmentClientComponent.create(c.component(), c.rarity(), c.icon(), c.paddingTop(), c.iconSize()));
        }
    }
}
