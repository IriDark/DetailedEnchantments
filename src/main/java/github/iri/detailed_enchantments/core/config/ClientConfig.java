package github.iri.detailed_enchantments.core.config;

import net.minecraftforge.common.*;
import org.apache.commons.lang3.tuple.*;

public class ClientConfig{
    public static ForgeConfigSpec.ConfigValue<Integer> ICON_SIZE, ICON_SIZE_MIN, ICON_DECREASING_COUNT;

    public static ForgeConfigSpec.ConfigValue<Boolean> SHIFT_FOR_DETAILS, ONLY_ENCHANTING_TABLE, ONLY_ITEMS;

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

        SHIFT_FOR_DETAILS = builder.comment("Is shift needed for description (Default: true)").define("shiftForDetails", true);
        ONLY_ITEMS = builder.comment("Display only on items (Default: false)").define("onlyItems", false);
        ONLY_ENCHANTING_TABLE = builder.comment("Display only on enchanting table (Default: false)").define("onlyEnchantingTable", false);
    }
}