package github.iri.detailed_enchantments.core.config;

import net.minecraftforge.common.*;
import org.apache.commons.lang3.tuple.*;

public class ClientConfig{
    public static ForgeConfigSpec.ConfigValue<Integer>
    ICON_SIZE, ICON_SIZE_MIN, ICON_DECREASING_COUNT,
    ENCHANTMENT_ICON_SIZE;

    public static ForgeConfigSpec.ConfigValue<Boolean> SHIFT_FOR_DETAILS, ONLY_ENCHANTING_TABLE, ONLY_ITEMS,

    SHOW_RARITY, SHOW_MAX_LEVEL, SHOW_TRADE_STATUS, SHOW_INCOMPATIBILITIES;

    static{
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }

    public static final ClientConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    public ClientConfig(ForgeConfigSpec.Builder builder){
        builder.push("ClientConfig");
        ICON_DECREASING_COUNT = builder.comment("Count on which icons will decrease to minimal size (Default: 3)").defineInRange("iconDecreaseCount", 3, 0, 64);
        ICON_SIZE = builder.comment("Normal size of icons (Default: 18)").defineInRange("iconSize", 18, 0, 64);
        ICON_SIZE_MIN = builder.comment("Minimal size of icons (Default: 12)").defineInRange("iconSizeMin", 12, 0, 64);
        ENCHANTMENT_ICON_SIZE = builder.comment("Size of icons (Default: 32)").defineInRange("enchantmentIconSize", 32, 0, 64);

        SHIFT_FOR_DETAILS = builder.comment("Is shift needed for description (Default: true)").define("shiftForDetails", true);
        ONLY_ITEMS = builder.comment("Display only on items (Default: false)").define("onlyItems", false);
        ONLY_ENCHANTING_TABLE = builder.comment("Display only on enchanting table (Default: false)").define("onlyEnchantingTable", false);

        SHOW_RARITY = builder.comment("Shows rarity of enchantment inside Enchantment screen (Default: true)").define("showRarity", true);
        SHOW_MAX_LEVEL = builder.comment("Shows max level of enchantment inside Enchantment screen (Default: true)").define("showMaxLevel", true);
        SHOW_INCOMPATIBILITIES = builder.comment("Shows incompatibilities of enchantment inside Enchantment screen (Default: true)").define("showIncompatibilities", true);
        SHOW_TRADE_STATUS = builder.comment("Shows trade status of enchantment inside Enchantment screen (Default: true)").define("showTradeStatus", true);
    }
}