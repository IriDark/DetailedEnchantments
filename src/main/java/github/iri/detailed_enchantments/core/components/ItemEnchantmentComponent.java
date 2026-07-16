package github.iri.detailed_enchantments.core.components;

import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.enchantment.*;

public record ItemEnchantmentComponent(MutableComponent component, Enchantment.Rarity rarity, ResourceLocation icon, boolean curse, int paddingTop, int iconSize) implements TooltipComponent{}