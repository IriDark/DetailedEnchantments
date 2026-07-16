package github.iri.detailed_enchantments;

import github.iri.detailed_enchantments.core.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class Events {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        if(ClientConfig.SHOW_ENCHANTABILITY.get()) {
            if (stack.getEnchantmentValue() > 0) {
                tooltip.add(1, Component.translatable("item.detailed_enchantments.enchantment_value", stack.getEnchantmentValue()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}