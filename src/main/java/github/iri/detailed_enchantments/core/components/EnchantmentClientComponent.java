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
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import org.jetbrains.annotations.*;
import org.joml.Matrix4f;

import java.util.*;

public class EnchantmentClientComponent implements ClientTooltipComponent{
    private final ResourceLocation icon;

    private record Section(int startY, int height, int width, SectionType type){}

    private enum SectionType{ICON, NAME, DESCRIPTION, PROTECTION, DAMAGE, SEPARATOR, STATS, INCOMPATS}

    private final List<FormattedCharSequence> descriptionLines;
    private final List<FormattedCharSequence> incompatibilityLines;
    private final List<Section> sections = new ArrayList<>();

    private final Enchantment enchantment;
    private final Enchantment.Rarity rarity;

    private final int enchLevel;
    private final int maxLevel;
    private final boolean tradeable;

    private final ItemStack itemStack;

    private final int iconSize = ClientConfig.ENCHANTMENT_ICON_SIZE.get();
    private final int verticalGap = 6;
    private final int sectionGap = 12;
    private final int statsGap = 8;

    private int totalWidth;
    private int totalHeight;

    public EnchantmentClientComponent(EnchantmentComponent comp){
        this.icon = comp.icon();
        this.rarity = comp.enchantment().getRarity();
        this.maxLevel = comp.enchantment().getMaxLevel();
        this.tradeable = comp.enchantment().isTradeable();
        this.enchantment = comp.enchantment();
        this.enchLevel = comp.level();
        this.itemStack = comp.stack();

        Font font = Minecraft.getInstance().font;
        this.descriptionLines = getVisualOrder(comp.description(), comp.description().getStyle(), font);

        Component incompLabel = comp.incompatibilities();
        if(incompLabel.getString().isEmpty()){
            this.incompatibilityLines = List.of();
        }else{
            this.incompatibilityLines = getVisualOrder(incompLabel, incompLabel.getStyle(), font);
        }

        computeLayout(font);
    }

    private static List<FormattedCharSequence> getVisualOrder(Component component, Style style, Font font){
        return Language.getInstance().getVisualOrder(font.getSplitter().splitLines(component, 200, style));
    }

    private void computeLayout(Font font) {
        int y = 0;
        List<Integer> widths = new ArrayList<>();

        // NAME
        sections.add(new Section(y, 12, 12, SectionType.NAME));
        y += 6;
        widths.add(iconSize);

        // ICON
        sections.add(new Section(y, iconSize, iconSize, SectionType.ICON));
        y += iconSize + verticalGap;
        widths.add(iconSize);

        // DESCRIPTION
        int descW = measureWidth(font, descriptionLines);
        int descH = measureHeight(descriptionLines);
        sections.add(new Section(y, descH, descW, SectionType.DESCRIPTION));
        y += descH;
        widths.add(descW);

        // DAMAGE BONUS
        MobType type = DEUtil.getEnchantmentTarget(enchantment, enchLevel);
        float bonus = enchantment.getDamageBonus(enchLevel, type, itemStack);
        float base = (float)DEUtil.getItemAttackDamage(itemStack, type);
        boolean showDamage = type != null && bonus > 0 && base > 1;

        if (showDamage) {
            Component dmg = getDamageComponent(type, base, bonus);
            int w = font.width(dmg);
            sections.add(new Section(y, 12, w, SectionType.DAMAGE));
            y += sectionGap;
            widths.add(w);
        }

        // PROTECTION BONUS
        if(enchantment instanceof ProtectionEnchantment protection){
            DamageSource source = DEUtil.getDamageSource(protection);
            int protectionBonus = protection.getDamageProtection(enchLevel, source);
            float baseProtection = 0f;
            boolean showProtection = protectionBonus > 0;

            if (showProtection) {
                int w = font.width(getProtectionComponent(protection, baseProtection, protectionBonus));
                sections.add(new Section(y, 12, w, SectionType.PROTECTION));
                y += sectionGap;
                widths.add(w);
            }
        }

        // SEPARATOR
        if (shouldShowStats()) {
            sections.add(new Section(y, 2, 100, SectionType.SEPARATOR));
            y += 8;
        }

        // STATS
        int statsW = measureStatsWidth(font);
        sections.add(new Section(y, 2, statsW, SectionType.STATS));
        y += sectionGap;
        widths.add(statsW);

        // INCOMPATS
        if (shouldShowIncompats()) {
            int w = measureWidth(font, incompatibilityLines);
            int h = 12 + measureHeight(incompatibilityLines);
            sections.add(new Section(y, h, w, SectionType.INCOMPATS));
            y += h;
            widths.add(w);
        }

        this.totalWidth = Math.max(100, widths.stream().max(Integer::compare).orElse(100));
        this.totalHeight = y;
    }

    private int measureWidth(Font font, List<FormattedCharSequence> lines) {
        int w = 0;
        for (var l : lines) w = Math.max(w, font.width(l));
        return w;
    }

    private boolean shouldShowStats(){
        return ClientConfig.SHOW_MAX_LEVEL.get() || ClientConfig.SHOW_RARITY.get() || ClientConfig.SHOW_TRADE_STATUS.get();
    }

    @Override
    public int getHeight(){
        return totalHeight;
    }

    @Override
    public int getWidth(Font font){
        return totalWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics g){
        for(Section s : sections){
            int drawY = y + s.startY();
            renderSectionImage(s, x, drawY, g);
        }
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f mat, MultiBufferSource.BufferSource buf){
        for(Section s : sections){
            int drawY = y + s.startY();
            renderSectionText(s, x, drawY, font, mat, buf);
        }
    }

    private void renderSectionImage(Section s, int x, int y, GuiGraphics g){
        switch(s.type){
            case ICON -> {
                int ix = x + (totalWidth - iconSize) / 2;
                g.blit(icon, ix, y, 0, 0, iconSize, iconSize, iconSize, iconSize);
            }

            case SEPARATOR -> {
                int mid = x + totalWidth / 2;
                int colSolid = 0xFF555555;
                int colClear = 0x00555555;
                drawHorizontalGradient(g, x, y, mid, y + 1, colClear, colSolid);
                drawHorizontalGradient(g, mid, y, x + totalWidth, y + 1, colSolid, colClear);
            }

            default -> {}
        }
    }

    private int measureHeight(List<FormattedCharSequence> lines) {
        return lines.size() * 12;
    }

    private int measureStatsWidth(Font font) {
        Component lvl = Component.translatable("detailed_enchantments.max_level", maxLevel);
        Component rar = Component.translatable("detailed_enchantments.rarity", rarityName());
        Component trd = getTradeableComponent();

        int w = 0;

        if (ClientConfig.SHOW_RARITY.get()) w += font.width(rar);
        if (ClientConfig.SHOW_MAX_LEVEL.get()) w += (w > 0 ? statsGap : 0) + font.width(lvl);
        if (ClientConfig.SHOW_TRADE_STATUS.get()) w += (w > 0 ? statsGap : 0) + font.width(trd);

        return w;
    }

    private void renderSectionText(Section s, int x, int y, Font font, Matrix4f mat, MultiBufferSource.BufferSource buf){
        switch(s.type){
            case NAME -> {
                Component name = enchantment.getFullname(enchLevel);
                int centeredX = getCenteredX(font, x, name);
                font.drawInBatch(name, centeredX, y - 8, 0xAAAAAA, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
            }

            case DESCRIPTION -> {
                int yy = y;
                for(var line : descriptionLines){
                    int lx = x + (totalWidth - font.width(line)) / 2;
                    font.drawInBatch(line, lx, yy, -1, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                    yy += 12;
                }
            }

            case PROTECTION -> {
                if(enchantment instanceof ProtectionEnchantment protection){
                    DamageSource source = DEUtil.getDamageSource(protection);
                    int protectionBonus = protection.getDamageProtection(enchLevel, source);
                    float baseProtection = 0f;
                    Component prot = getProtectionComponent(protection, protectionBonus, baseProtection);

                    int lx = x + (totalWidth - font.width(prot)) / 2;
                    font.drawInBatch(prot, lx, y, 0xAAAAAA, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                }
            }

            case DAMAGE -> {
                MobType type = DEUtil.getEnchantmentTarget(enchantment, enchLevel);
                float bonus = enchantment.getDamageBonus(enchLevel, type, itemStack);
                float base = (float)DEUtil.getItemAttackDamage(itemStack, type);

                Component dmg = getDamageComponent(type, base, bonus);

                int lx = x + (totalWidth - font.width(dmg)) / 2;
                font.drawInBatch(dmg, lx, y, 0xAAAAAA, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
            }

            case STATS -> {
                Component rar = Component.translatable("detailed_enchantments.rarity", rarityName());
                Component lvl = Component.translatable("detailed_enchantments.max_level", maxLevel);
                Component trd = getTradeableComponent();

                int rowW = measureStatsWidth(font);
                int drawX = x + (totalWidth - rowW) / 2;

                if(ClientConfig.SHOW_RARITY.get()){
                    font.drawInBatch(rar, drawX, y, rarityColor(), true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                    drawX += font.width(rar) + statsGap;
                }

                if(ClientConfig.SHOW_MAX_LEVEL.get()){
                    font.drawInBatch(lvl, drawX, y, 0xFFAA00, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                    drawX += font.width(lvl) + statsGap;
                }

                if(ClientConfig.SHOW_TRADE_STATUS.get()){
                    int c = tradeable ? 0x55FF55 : 0xFF5555;
                    font.drawInBatch(trd, drawX, y, c, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                }
            }

            case INCOMPATS -> {
                Component header = Component.translatable("detailed_enchantments.incompatible");
                int hx = x + (totalWidth - font.width(header)) / 2;
                font.drawInBatch(header, hx, y, 0xAAAAAA, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);

                int yy = y + 12;
                for(var line : incompatibilityLines){
                    int lx = x + (totalWidth - font.width(line)) / 2;
                    font.drawInBatch(line, lx, yy, 0xFF7777, true, mat, buf, Font.DisplayMode.NORMAL, 0, 15728880);
                    yy += 12;
                }
            }

            default -> {}
        }
    }

    private Component rarityName(){
        return DEUtil.getRarity(rarity);
    }

    private Component getProtectionComponent(ProtectionEnchantment ench, float base, float bonus) {
        int percentage =  (int) base + (int) (bonus * 4);
        return Component.translatable("detailed_enchantments.reduction").withStyle(ChatFormatting.GRAY).append(Component.literal("+" + percentage + "%").withStyle(ChatFormatting.BLUE));
    }

    private static Component getDamageComponent(MobType type, float base, float bonus){
        return Component.translatable("detailed_enchantments.damage_bonus", DEUtil.getTypeName(type).getString()).withStyle(ChatFormatting.GRAY).append(Component.literal(base + " -> " +  (base + bonus)).withStyle(ChatFormatting.BLUE));
    }

    private Component getTradeableComponent(){
        return tradeable ? Component.translatable("detailed_enchantments.tradeable") : Component.translatable("detailed_enchantments.not_tradeable");
    }

    private record ColorResult(float a, float r, float g, float b){ }
    private ColorResult color(int c){
        float a = ((c >> 24) & 255) / 255f;
        float r = ((c >> 16) & 255) / 255f;
        float g = ((c >> 8) & 255) / 255f;
        float b = (c & 255) / 255f;
        return new ColorResult(a, r, g, b);
    }

    private int rarityColor(){
        return switch(rarity){
            case COMMON -> 0xdbe1e9;
            case UNCOMMON -> 0xb9ddcb;
            case RARE -> 0x9096ff;
            case VERY_RARE -> 0xdba6ff;
        };
    }

    private int getCenteredX(Font font, int x, Component name){
        return x + (totalWidth - font.width(name)) / 2;
    }

    private boolean shouldShowIncompats(){
        return !incompatibilityLines.isEmpty() && ClientConfig.SHOW_INCOMPATIBILITIES.get();
    }

    private void drawHorizontalGradient(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int colorStart, int colorEnd){
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix = guiGraphics.pose().last().pose();
        ColorResult result = color(colorStart);
        ColorResult endResult = color(colorEnd);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(endResult.r(), endResult.g(), endResult.b(), endResult.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(result.r(), result.g(), result.b(), result.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(result.r(), result.g(), result.b(), result.a()).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(endResult.r(), endResult.g(), endResult.b(), endResult.a()).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }
}