package github.iri.detailed_enchantments;

import com.mojang.logging.LogUtils;
import github.iri.detailed_enchantments.core.config.*;
import github.iri.detailed_enchantments.core.events.*;
import net.minecraft.resources.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.*;
import org.slf4j.Logger;

@Mod(DetailedEnchantments.ID)
public class DetailedEnchantments{
    public static final String ID = "detailed_enchantments";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(DetailedEnchantments.ID, path);
    }

    public DetailedEnchantments(){
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfig.SPEC);

        forgeBus.register(new ClientEvents());
    }
}