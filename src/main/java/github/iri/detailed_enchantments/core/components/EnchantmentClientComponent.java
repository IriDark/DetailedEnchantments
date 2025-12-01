package github.iri.detailed_enchantments.core.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import github.iri.detailed_enchantments.core.*;
import github.iri.detailed_enchantments.core.config.*;
import net.minecraft.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.*;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class EnchantmentClientComponent implements ClientTooltipComponent {
    private final ResourceLocation icon;
    private final Enchantment.Rarity rarity;
    private final List<FormattedCharSequence> descriptionLines;
    private final List<FormattedCharSequence> incompatibilityLines;

    private final Enchantment enchantment;
    private final int enchLevel;

    private final ItemStack itemStack;
    private final int maxLevel;
    private final boolean isTradeable;

    private final int iconSize = ClientConfig.ENCHANTMENT_ICON_SIZE.get();
    private final int verticalGap = 6;
    private final int sectionGap = 12;
    private final int statsGap = 8;

    private final int totalWidth;
    private final int totalHeight;

    private final int descStartY;
    private final int separatorYOffset;
    private final int statsStartY;

    //todo refactor
    public EnchantmentClientComponent(EnchantmentComponent comp) {
        this.icon = comp.icon();
        this.rarity = comp.enchantment().getRarity();
        this.maxLevel = comp.enchantment().getMaxLevel();
        this.isTradeable = comp.enchantment().isTradeable();
        this.enchantment = comp.enchantment();
        this.enchLevel = comp.level();
        this.itemStack = comp.stack();

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

        int hDesc = 0;
        MobType type = DEUtil.getEnchantmentTarget(enchantment, enchLevel);
        float attackBonus = enchantment.getDamageBonus(enchLevel, type, itemStack);
        float currentDamage = (float)DEUtil.getItemAttackDamage(itemStack, type);
        if(type != null && attackBonus > 0 && currentDamage > 1) {
            Component line = getDamageBonusComponent(type, currentDamage, attackBonus);
            wDesc += Math.max(wDesc, font.width(line)) / 2;
            hDesc = 12;
        }

        int wIncomp = 0;
        if(shouldShowIncompats()){
            for(var line : incompatibilityLines) wIncomp = Math.max(wIncomp, font.width(line));
        }

        Component rarityName = Component.translatable("detailed_enchantments.rarity", getRarityName(this.rarity));
        Component lvlText = Component.translatable("detailed_enchantments.max_level", maxLevel);
        Component tradeText = isTradeable ? Component.translatable("detailed_enchantments.tradeable") : Component.translatable("detailed_enchantments.not_tradeable");
        int wStats = getRowWidth(font, lvlText, rarityName, tradeText);

        this.totalWidth = Math.max(100, Math.max(wDesc, Math.max(wStats, wIncomp)));
        int currentY = iconSize + verticalGap;

        this.descStartY = currentY;
        currentY += (descriptionLines.size() * 9) + sectionGap;

        this.separatorYOffset = currentY;
        if(shouldShowSection()) currentY += 3;

        this.statsStartY = currentY + 4;
        if(shouldShowSection()) currentY += 9 + sectionGap;
        if (shouldShowIncompats()) {
            currentY += 9 + 4 + (incompatibilityLines.size() * 9);
        }

        this.totalHeight = currentY + hDesc;
    }

    private int getRowWidth(Font font, Component lvlText, Component rarityName, Component tradeText){
        int wStats = 0;
        if(ClientConfig.SHOW_MAX_LEVEL.get())  wStats += font.width(lvlText);
        if(ClientConfig.SHOW_RARITY.get()) wStats += statsGap + font.width(rarityName);
        if(ClientConfig.SHOW_TRADE_STATUS.get()) wStats += statsGap + font.width(tradeText);
        return wStats;
    }

    private static Component getDamageBonusComponent(MobType type, float currentDamage, float attackBonus){
        return Component.translatable("detailed_enchantments.damage_bonus", DEUtil.getTypeName(type).getString(), currentDamage, (currentDamage + attackBonus)).withStyle(ChatFormatting.DARK_GRAY);
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
        guiGraphics.blit(this.icon, iconX, y + 4, 0, 0, iconSize, iconSize, iconSize, iconSize);

        MobType type = DEUtil.getEnchantmentTarget(enchantment, enchLevel);
        float attackBonus = enchantment.getDamageBonus(enchLevel, type, itemStack);
        float currentDamage = (float)DEUtil.getItemAttackDamage(itemStack, type);
        if(type != null && attackBonus > 0 && currentDamage > 1) {
            y += 12;
        }

        if(shouldShowIncompats() || shouldShowSection()){
            int sepY = y + separatorYOffset - 2;
            int centerX = x + (totalWidth / 2);
            int colorSolid = 0xFF555555;
            int colorClear = 0x00555555;

            drawHorizontalGradient(guiGraphics, x, sepY, centerX, sepY + 1, colorClear, colorSolid);
            drawHorizontalGradient(guiGraphics, centerX, sepY, x + totalWidth, sepY + 1, colorSolid, colorClear);
        }
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        Component name = enchantment.getFullname(enchLevel);
        int centeredX = x + (totalWidth - font.width(name)) / 2;
        font.drawInBatch(name, centeredX, y - 8, 0xAAAAAA, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        int currentTextY = y + descStartY;
        for (FormattedCharSequence line : descriptionLines) {
            int lineX = x + (totalWidth - font.width(line)) / 2; // Center Align
            font.drawInBatch(line, lineX, currentTextY, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            currentTextY += 12;
        }

        MobType type = DEUtil.getEnchantmentTarget(enchantment, enchLevel);
        float attackBonus = enchantment.getDamageBonus(enchLevel, type, itemStack);
        float currentDamage = (float)DEUtil.getItemAttackDamage(itemStack, type);
        if(type != null && attackBonus > 0 && currentDamage > 1) {
            Component line = getDamageBonusComponent(type, currentDamage, attackBonus);
            int lineX = x + (totalWidth - font.width(line)) / 2;
            font.drawInBatch(line, lineX, currentTextY, 0xAAAAAA, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            y += 12;
        }

        int statsY = y + statsStartY;
        Component rarityName = Component.translatable("detailed_enchantments.rarity", getRarityName(this.rarity));
        Component lvlText = Component.translatable("detailed_enchantments.max_level", maxLevel);
        Component tradeText = isTradeable ? Component.translatable("detailed_enchantments.tradeable") : Component.translatable("detailed_enchantments.not_tradeable");
        int rowWidth = getRowWidth(font, lvlText, rarityName, tradeText);
        int drawX = x + (totalWidth - rowWidth) / 2;

        if(ClientConfig.SHOW_RARITY.get()){
            font.drawInBatch(rarityName, drawX, statsY, getRarityColor(this.rarity), true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            drawX += font.width(rarityName) + statsGap;
        }

        if(ClientConfig.SHOW_MAX_LEVEL.get()){
            font.drawInBatch(lvlText, drawX, statsY, 0xFFAA00, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            drawX += font.width(lvlText) + statsGap;
        }

        if(ClientConfig.SHOW_TRADE_STATUS.get()){
            int tradeColor = isTradeable ? 0x55FF55 : 0xFF5555;
            font.drawInBatch(tradeText, drawX, statsY, tradeColor, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        if (shouldShowIncompats()) {
            int incompY = statsY + (shouldShowSection() ? statsGap + 6 : 0);

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

    private boolean shouldShowSection() {
        return ClientConfig.SHOW_MAX_LEVEL.get() || ClientConfig.SHOW_RARITY.get() || ClientConfig.SHOW_TRADE_STATUS.get();
    }

    private boolean shouldShowIncompats(){
        return !incompatibilityLines.isEmpty() && ClientConfig.SHOW_INCOMPATIBILITIES.get();
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