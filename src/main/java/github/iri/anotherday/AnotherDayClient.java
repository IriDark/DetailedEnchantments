package github.iri.anotherday;

import github.iri.anotherday.client.render.*;
import github.iri.anotherday.registries.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.event.lifecycle.*;
import pro.komaru.tridot.client.render.gui.*;

public class AnotherDayClient{

    public static void setupClient(){
        SplashHandler.add("Squirrels!");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryEvents{

        @SubscribeEvent
        public static void doClientStuff(FMLClientSetupEvent event){
            EntityRenderers.register(EntityTypeRegistry.SQUIRREL.get(), SquirrelRenderer::new);
        }
    }
}