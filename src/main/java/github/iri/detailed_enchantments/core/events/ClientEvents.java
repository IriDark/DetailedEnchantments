package github.iri.detailed_enchantments.core.events;

import com.mojang.datafixers.util.*;
import github.iri.detailed_enchantments.*;
import github.iri.detailed_enchantments.core.*;
import github.iri.detailed_enchantments.core.components.*;
import github.iri.detailed_enchantments.core.config.*;
import net.minecraft.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.registries.*;

import java.util.*;

public class ClientEvents{

    @SubscribeEvent
    public void onTooltipGatherComponents(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        ItemStack stack = event.getItemStack();
        var enchMap = EnchantmentHelper.getEnchantments(stack);

        if (ClientConfig.ONLY_ENCHANTING_TABLE.get()) return;
        if (enchMap.isEmpty()) return;

        if (!ClientConfig.SHIFT_FOR_DETAILS.get() || Screen.hasShiftDown()) {
            for (Map.Entry<Enchantment, Integer> entry : enchMap.entrySet()) {
                Component descriptionText = DEUtil.getDescription(entry.getKey());
                var key = ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey());
                if(key == null) continue;

                ResourceLocation texture = new ResourceLocation(key.getNamespace(), "textures/gui/tooltips" + "/" + key.getPath() + ".png");

                var enchantment = entry.getKey();
                int enchIndex = findTextIndex(elements, enchantment.getDescriptionId());
                int size = getSize(enchMap);
                if (enchIndex >= 0 && enchIndex <= elements.size()) {
                    elements.add(enchIndex + 1, Either.right(new ItemEnchantmentComponent(descriptionText.copy(), enchantment.getRarity(), texture, 0, size)));
                }
            }
        } else {
            elements.add(1, Either.right(new ItemEnchantmentComponent(
            Component.translatable("detailed_enchantments.shift_for_details").withStyle(ChatFormatting.DARK_GRAY), Enchantment.Rarity.COMMON, DetailedEnchantments.loc("textures/gui/tooltips/info_box.png"), 0, 10)));
        }
    }

    private int findTextIndex(List<Either<FormattedText, TooltipComponent>> list, String text) {
        for (int i = 0; i < list.size(); i++) {
            var either = list.get(i);
            if (either.left().isPresent()) {
                FormattedText ft = either.left().get();
                if (ft instanceof MutableComponent comp && comp.getContents() instanceof TranslatableContents translatable && translatable.getKey().equals(text)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static int getSize(Map<Enchantment, Integer> enchList){
        int size = ClientConfig.ICON_SIZE.get();
        if(enchList.size() > ClientConfig.ICON_DECREASING_COUNT.get()) size = ClientConfig.ICON_SIZE_MIN.get();
        return size;
    }
}