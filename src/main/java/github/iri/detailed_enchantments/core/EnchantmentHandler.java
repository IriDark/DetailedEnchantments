package github.iri.detailed_enchantments.core;

import github.iri.detailed_enchantments.core.components.*;
import net.minecraft.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraftforge.registries.*;

import java.util.*;

public class EnchantmentHandler{
    private static final Map<Enchantment, Component> INCOMPATIBILITY_CACHE = new HashMap<>();

    public static void handle(GuiGraphics guiGraphics, int mouseX, int mouseY, Enchantment enchantment, int level, ItemStack stack, List<Component> list, ResourceLocation key){
        Component descriptionText = DEUtil.getDescription(enchantment);
        var texture = new ResourceLocation(key.getNamespace(), "textures/gui/tooltips/" + key.getPath() + ".png");

        Optional<TooltipComponent> component;
        component = Optional.of(new EnchantmentComponent(descriptionText.copy(), enchantment, level, stack, texture, getCachedIncompatibilities(enchantment)));
        guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), component, mouseX, mouseY);
    }

    public static Component getCachedIncompatibilities(Enchantment e) {
        return INCOMPATIBILITY_CACHE.computeIfAbsent(e, key -> {
            MutableComponent comp = Component.empty();
            List<Component> names = new ArrayList<>();
            for (var entry : ForgeRegistries.ENCHANTMENTS) {
                if (!key.isCompatibleWith(entry) && key != entry) {
                    names.add(Component.translatable(entry.getDescriptionId()).withStyle(ChatFormatting.RED));
                }
            }

            if (names.isEmpty()) return Component.empty();
            for (int i = 0; i < names.size(); i++) {
                comp.append(names.get(i));
                if (i < names.size() - 1) {
                    comp.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                }
            }
            return comp;
        });
    }}
