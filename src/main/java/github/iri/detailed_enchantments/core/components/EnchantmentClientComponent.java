package github.iri.detailed_enchantments.core.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.*;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class EnchantmentClientComponent implements ClientTooltipComponent {
    private final ResourceLocation icon;
    private final Enchantment.Rarity rarity;
    private final List<FormattedCharSequence> descriptionLines;
    private final List<FormattedCharSequence> incompatibilityLines;

    private final int maxLevel;
    private final boolean isTradeable;

    private final int iconSize = 32;
    private final int verticalGap = 4;
    private final int sectionGap = 8;

    private final int totalWidth;
    private final int totalHeight;

    private final int descStartY;
    private final int separatorYOffset;
    private final int statsStartY;

    public EnchantmentClientComponent(EnchantmentComponent comp) {
        this.icon = comp.icon();
        this.rarity = comp.rarity();
        this.maxLevel = comp.maxLevel();
        this.isTradeable = comp.isTradeable();

        Font font = Minecraft.getInstance().font;

        this.descriptionLines = Language.getInstance().getVisualOrder(
        font.getSplitter().splitLines(comp.description(), 200, comp.description().getStyle())
        );

        Component incompLabel = comp.incompatibilities();
        if (incompLabel.getString().isEmpty()) {
            this.incompatibilityLines = List.of();
        } else {
            this.incompatibilityLines = Language.getInstance().getVisualOrder(
            font.getSplitter().splitLines(incompLabel, 200, incompLabel.getStyle())
            );
        }

        int wDesc = 0;
        for (var line : descriptionLines) wDesc = Math.max(wDesc, font.width(line));

        int wIncomp = 0;
        for (var line : incompatibilityLines) wIncomp = Math.max(wIncomp, font.width(line));

        Component rarityName = Component.translatable("detailed_enchantments.rarity", getRarityName(this.rarity));
        Component lvlText = Component.translatable("detailed_enchantments.max_level", maxLevel);
        Component tradeText = isTradeable ? Component.translatable("detailed_enchantments.tradeable") : Component.translatable("detailed_enchantments.not_tradeable");
        int wStats = font.width(rarityName) + 8 + font.width(lvlText) + 8 + font.width(tradeText);
        this.totalWidth = Math.max(100, Math.max(wDesc, Math.max(wStats, wIncomp)));
        int currentY = 0;

        currentY += iconSize + verticalGap;

        this.descStartY = currentY;
        currentY += (descriptionLines.size() * 9) + sectionGap;

        this.separatorYOffset = currentY;
        currentY += 3;

        this.statsStartY = currentY;
        currentY += 9 + sectionGap;
        if (!incompatibilityLines.isEmpty()) {
            currentY += 9 + 4 + (incompatibilityLines.size() * 9);
        }

        this.totalHeight = currentY + verticalGap;
    }

    @Override
    public int getHeight() {
        return totalHeight;
    }

    @Override
    public int getWidth(Font font) {
        return totalWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        RenderSystem.enableBlend();
        int iconX = x + (totalWidth - iconSize) / 2;
        guiGraphics.blit(this.icon, iconX, y, 0, 0, iconSize, iconSize, iconSize, iconSize);

        int sepY = y + separatorYOffset - 2;
        int centerX = x + (totalWidth / 2);
        int colorSolid = 0xFF555555;
        int colorClear = 0x00555555;

        drawHorizontalGradient(guiGraphics, x, sepY, centerX, sepY + 1, colorClear, colorSolid);
        drawHorizontalGradient(guiGraphics, centerX, sepY, x + totalWidth, sepY + 1, colorSolid, colorClear);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        int currentTextY = y + descStartY;
        for (FormattedCharSequence line : descriptionLines) {
            int lineX = x + (totalWidth - font.width(line)) / 2; // Center Align
            font.drawInBatch(line, lineX, currentTextY, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            currentTextY += 9;
        }

        int statsY = y + statsStartY;
        Component rarityName = Component.translatable("detailed_enchantments.rarity", getRarityName(this.rarity));
        Component lvlText = Component.translatable("detailed_enchantments.max_level", maxLevel);
        Component tradeText = isTradeable ? Component.translatable("detailed_enchantments.tradeable") : Component.translatable("detailed_enchantments.not_tradeable");

        int gap = 8;
        int rowWidth = font.width(rarityName) + gap + font.width(lvlText) + gap + font.width(tradeText);
        int drawX = x + (totalWidth - rowWidth) / 2;

        font.drawInBatch(rarityName, drawX, statsY, getRarityColor(this.rarity), true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        drawX += font.width(rarityName) + gap;

        font.drawInBatch(lvlText, drawX, statsY, 0xFFAA00, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        drawX += font.width(lvlText) + gap;

        int tradeColor = isTradeable ? 0x55FF55 : 0xFF5555;
        font.drawInBatch(tradeText, drawX, statsY, tradeColor, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        if (!incompatibilityLines.isEmpty()) {
            int incompY = statsY + 14;

            Component header = Component.translatable("detailed_enchantments.incompatible");
            int headerX = x + (totalWidth - font.width(header)) / 2;
            font.drawInBatch(header, headerX, incompY, 0xAAAAAA, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);

            incompY += 10;
            for (FormattedCharSequence line : incompatibilityLines) {
                int lineX = x + (totalWidth - font.width(line)) / 2;
                font.drawInBatch(line, lineX, incompY, 0xFF7777, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
                incompY += 9;
            }
        }
    }

    private void drawHorizontalGradient(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int colorStart, int colorEnd) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix = guiGraphics.pose().last().pose();
        ColorResult result = getColorResult(colorStart);
        ColorResult endResult = getColorResult(colorEnd);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(endResult.r(), endResult.g(), endResult.b(), endResult.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(result.r(), result.g(), result.b(), result.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(result.r(), result.g(), result.b(), result.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(endResult.r(), endResult.g(), endResult.b(), endResult.a()).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

    private static @NotNull ColorResult getColorResult(int color){
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        return new ColorResult(a, r, g, b);
    }

    private record ColorResult(float a, float r, float g, float b){}

    private int getRarityColor(Enchantment.Rarity rarity) {
        return switch (rarity) {
            case COMMON -> 0xdbe1e9;
            case UNCOMMON -> 0xb9ddcb;
            case RARE -> 0x9096ff;
            case VERY_RARE -> 0xdba6ff;
        };
    }

    private String getRarityName(Enchantment.Rarity rarity) {
        String s = rarity.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}