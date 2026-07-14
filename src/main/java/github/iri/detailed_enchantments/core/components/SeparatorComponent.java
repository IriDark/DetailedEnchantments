package github.iri.detailed_enchantments.core.components;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SeparatorComponent(Component component) implements TooltipComponent {
}