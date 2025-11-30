package github.iri.detailed_enchantments.core;

import net.minecraft.*;
import net.minecraft.client.resources.language.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraftforge.registries.*;

import java.util.*;
import java.util.concurrent.*;

public class DEUtil{
    private static final Map<Enchantment, MutableComponent> DESCRIPTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<Enchantment, List<Item>> APPLICABLE_ITEMS_CACHE = new ConcurrentHashMap<>();

    public static MutableComponent getDescription(Enchantment ench) {
        if(ench == null) return Component.empty();
        return DESCRIPTION_CACHE.computeIfAbsent(ench, e -> {
            String descriptionKey = e.getDescriptionId() + ".desc";
            if (!I18n.exists(descriptionKey) && I18n.exists(e.getDescriptionId() + ".description")) {
                descriptionKey = e.getDescriptionId() + ".description";
            }

            return Component.translatable(descriptionKey).withStyle(ChatFormatting.DARK_GRAY);
        });
    }

    /**
     * Gets the list of applicable items for an enchantment,
     * computing and caching it if not already done.
     */
    public static List<Item> getApplicableItems(Enchantment enchantment) {
        return APPLICABLE_ITEMS_CACHE.computeIfAbsent(enchantment, e -> {
            List<Item> items = new ArrayList<>();
            for (Item item : ForgeRegistries.ITEMS) {
                int i = 0;
                if(i >= 40) return items;
                if (e.canEnchant(item.getDefaultInstance())) {
                    items.add(item);
                    i++;
                }
            }
            return items;
        });
    }
}
