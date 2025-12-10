package github.iri.detailed_enchantments.core;

import com.google.common.collect.*;
import com.mojang.blaze3d.systems.*;
import github.iri.detailed_enchantments.*;
import net.minecraft.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.language.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.registries.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class DEUtil{
    private static final Map<Enchantment, MutableComponent> DESCRIPTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<Enchantment, List<Item>> APPLICABLE_ITEMS_CACHE = new ConcurrentHashMap<>();

    public static Component getTypeName(MobType type) {
        if(type == MobType.UNDEAD) {
            return Component.translatable("detailed_enchantments.undead");
        }
        if(type == MobType.ARTHROPOD) {
            return Component.translatable("detailed_enchantments.arthropod");
        }
        if(type == MobType.ILLAGER) {
            return Component.translatable("detailed_enchantments.illager");
        }
        if(type == MobType.WATER) {
            return Component.translatable("detailed_enchantments.water");
        }

        return Component.translatable("detailed_enchantments.all");
    }

    public static Component getRarity(Enchantment.Rarity rarity) {
        if(rarity == Enchantment.Rarity.COMMON) {
            return Component.translatable("detailed_enchantments.common");
        }
        if(rarity == Enchantment.Rarity.UNCOMMON) {
            return Component.translatable("detailed_enchantments.uncommon");
        }
        if(rarity == Enchantment.Rarity.RARE) {
            return Component.translatable("detailed_enchantments.rare");
        }
        if(rarity == Enchantment.Rarity.VERY_RARE) {
            return Component.translatable("detailed_enchantments.very_rare");
        }

        return Component.translatable("detailed_enchantments.all");
    }

    @OnlyIn(Dist.CLIENT)
    public static DamageSource getDamageSource(ProtectionEnchantment pEnchantment) {
        Level level = Minecraft.getInstance().level;
        DamageSources sources = level.damageSources();
        if (pEnchantment.type == ProtectionEnchantment.Type.FIRE) {
            return sources.inFire();
        } else if (pEnchantment.type == ProtectionEnchantment.Type.FALL) {
            return sources.fall();
        } else if (pEnchantment.type == ProtectionEnchantment.Type.EXPLOSION) {
            return sources.explosion(null);
        } else if (pEnchantment.type == ProtectionEnchantment.Type.PROJECTILE) {
            return sources.mobProjectile(null, null);
        }

        return sources.generic();
    }

    public static MobType getEnchantmentTarget(Enchantment enchant, int level) {
        if (enchant.getDamageBonus(level, MobType.UNDEAD) > 0 && enchant.getDamageBonus(level, MobType.UNDEFINED) == 0) {
            return MobType.UNDEAD;
        }
        if (enchant.getDamageBonus(level, MobType.ARTHROPOD) > 0 && enchant.getDamageBonus(level, MobType.UNDEFINED) == 0) {
            return MobType.ARTHROPOD;
        }
        if (enchant.getDamageBonus(level, MobType.ILLAGER) > 0 && enchant.getDamageBonus(level, MobType.UNDEFINED) == 0) {
            return MobType.ILLAGER;
        }

        if (enchant.getDamageBonus(level, MobType.WATER) > 0 && enchant.getDamageBonus(level, MobType.UNDEFINED) == 0) {
            return MobType.WATER;
        }

        if (enchant.getDamageBonus(level, MobType.UNDEFINED) > 0) {
            return MobType.UNDEFINED;
        }

        return null;
    }

    public static ResourceLocation getDefaultTexture(Enchantment.Rarity rarity) {
        return DetailedEnchantments.loc("textures/gui/tooltips/enchantment_" + rarity.name().toLowerCase() + ".png");
    }

    public static ResourceLocation getEnchantmentIcon(ResourceLocation tex, Enchantment.Rarity rarity) {
        var defaultTex = getDefaultTexture(rarity);
        var resourceManager = Minecraft.getInstance().getResourceManager();
        return resourceManager.getResource(tex).isPresent() ? tex : defaultTex;
    }

    public static double getItemAttackDamage(ItemStack stack, MobType targetType) {
        double attributeDamage = getAttributeDamage(stack);
        float enchantmentDamage = EnchantmentHelper.getDamageBonus(stack, targetType);

        return attributeDamage + enchantmentDamage;
    }

    public static double getAttributeDamage(ItemStack stack) {
        double damage = 1.0;
        Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        Collection<AttributeModifier> damageModifiers = modifiers.get(Attributes.ATTACK_DAMAGE);
        for (AttributeModifier modifier : damageModifiers) {
            double amount = modifier.getAmount();
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                damage += amount;
            } else if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                damage += (damage * amount);
            } else if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                damage *= (1.0 + amount);
            }
        }

        return damage;
    }

    public static MutableComponent getDescription(Enchantment ench) {
        if(ench == null) return Component.empty();
        return DESCRIPTION_CACHE.computeIfAbsent(ench, e -> {
            String descriptionKey = e.getDescriptionId() + ".desc";
            if (!I18n.exists(descriptionKey) && I18n.exists(e.getDescriptionId() + ".description")) {
                descriptionKey = e.getDescriptionId() + ".description";
            }

            return Component.translatable(descriptionKey).withStyle(ChatFormatting.DARK_GRAY);
        });
    }

    /**
     * Gets the list of applicable items for an enchantment,
     * computing and caching it if not already done.
     */
    public static List<Item> getApplicableItems(Enchantment enchantment) {
        return APPLICABLE_ITEMS_CACHE.computeIfAbsent(enchantment, e -> {
            List<Item> items = new ArrayList<>();
            for (Item item : ForgeRegistries.ITEMS) {
                int i = 0;
                if(i >= 40) return items;
                if (e.canEnchant(item.getDefaultInstance())) {
                    items.add(item);
                    i++;
                }
            }
            return items;
        });
    }
}
