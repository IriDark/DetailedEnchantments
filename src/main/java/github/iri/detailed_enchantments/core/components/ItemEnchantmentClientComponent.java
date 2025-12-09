package github.iri.detailed_enchantments.core.components;

import com.mojang.blaze3d.systems.*;
import github.iri.detailed_enchantments.*;
import github.iri.detailed_enchantments.core.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.renderer.*;
import net.minecraft.locale.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.item.enchantment.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class ItemEnchantmentClientComponent implements ClientTooltipComponent{
    public final ResourceLocation bg = DetailedEnchantments.loc("textures/gui/tooltips/background.png");
    public final ResourceLocation icon;
    private final Enchantment.Rarity rarity;

    public final int maxChars = 225;
    public final int iconMargin = 6;
    public final int iconSize;
    public final int paddingTop;

    public final List<FormattedCharSequence> lines;
    public ItemEnchantmentClientComponent(MutableComponent text, Enchantment.Rarity rarity, ResourceLocation icon, int iconSize, int paddingTop) {
        this.lines = Language.getInstance().getVisualOrder(Minecraft.getInstance().font.getSplitter().splitLines(text, maxChars, text.getStyle()));
        this.iconSize = iconSize;
        this.paddingTop = paddingTop;
        this.icon = icon;
        this.rarity = rarity;
    }

    public static ClientTooltipComponent create(MutableComponent text, Enchantment.Rarity rarity, ResourceLocation icon, int paddingTop, int iconSize) {
        return new ItemEnchantmentClientComponent(text, rarity, icon, iconSize, paddingTop);
    }

    @Override
    public int getHeight() {
        return Math.max(iconSize + iconMargin, (10 * lines.size())) + paddingTop;
    }

    @Override
    public int getWidth(Font pFont) {
        int width = 0;
        for (final FormattedCharSequence line : lines) {
            float scale = 1;
            int lineWidth = iconSize + iconMargin + (int) (pFont.width(line) * scale);
            if (lineWidth > width) {
                width = lineWidth;
            }
        }

        return width;
    }

    @Override
    public void renderText(Font pFont, int pMouseX, int pMouseY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {
        final int x = pMouseX + iconSize + 4;
        int y = pMouseY + paddingTop;
        for (final FormattedCharSequence line : lines) {
            float scale = 1;
            Matrix4f scaled = new Matrix4f(pMatrix);
            scaled.scale(scale, scale, 1);
            pFont.drawInBatch(line, x / scale, (y / scale) + 1, -1, true, scaled, pBufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            y += 9;
        }
    }

    @Override
    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        RenderSystem.enableBlend();
        pGuiGraphics.blit(bg, pX, pY + (paddingTop) - 1, 0, 0, iconSize, iconSize, iconSize, iconSize);
        pGuiGraphics.blit(DEUtil.getEnchantmentIcon(icon, this.rarity), pX, pY + (paddingTop) - 1, 0, 0, iconSize, iconSize, iconSize, iconSize);
        RenderSystem.disableBlend();
    }
}