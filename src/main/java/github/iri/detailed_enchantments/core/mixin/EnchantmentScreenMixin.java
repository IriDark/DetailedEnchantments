package github.iri.detailed_enchantments.core.mixin;

import github.iri.detailed_enchantments.core.*;
import github.iri.detailed_enchantments.core.components.*;
import github.iri.detailed_enchantments.core.config.*;
import net.minecraft.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraftforge.registries.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin{

    @Shadow
    private ItemStack last;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, boolean flag, int i, int j, int k, Enchantment enchantment, int l, int i1, List<Component> list) {
        if(ClientConfig.ONLY_ITEMS.get()) return;

        ci.cancel();
        var key = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if(key == null) return;

        EnchantmentHandler.handle(guiGraphics, mouseX, mouseY, enchantment, l, last, list, key);
    }
}
