package github.iri.detailed_enchantments.core.components;

import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.enchantment.*;

public record EnchantmentComponent(MutableComponent description, Enchantment.Rarity rarity, ResourceLocation icon, int maxLevel, boolean isTradeable, Component incompatibilities) implements TooltipComponent{ }