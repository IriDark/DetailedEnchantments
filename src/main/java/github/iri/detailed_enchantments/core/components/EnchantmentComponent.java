package github.iri.detailed_enchantments.core.components;

import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;

public record EnchantmentComponent(MutableComponent description, Enchantment enchantment, int level, ItemStack stack, ResourceLocation icon, Component incompatibilities) implements TooltipComponent{ }