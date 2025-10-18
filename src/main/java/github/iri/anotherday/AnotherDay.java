package github.iri.anotherday;

import com.mojang.logging.LogUtils;
import github.iri.anotherday.core.config.*;
import github.iri.anotherday.core.data.*;
import github.iri.anotherday.core.data.base.*;
import github.iri.anotherday.registries.*;
import github.iri.anotherday.registries.entities.*;
import net.minecraft.data.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.*;
import net.minecraftforge.data.event.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AnotherDay.ID)
public class AnotherDay{
    public static final String ID = "anotherday";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AnotherDay(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EntityTypeRegistry.register(modEventBus);
        BlockRegistry.load(modEventBus);
        ItemRegistry.load(modEventBus);
        BlockEntitiesRegistry.register(modEventBus);
        MiscRegistry.init(modEventBus);
        TabRegistry.CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        modEventBus.addListener(TabRegistry::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(ID, path);
    }

    private void setup(final FMLCommonSetupEvent event){

    }

    private void clientSetup(FMLClientSetupEvent event){
        AnotherDayClient.setupClient();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents{

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(() -> {
                SpawnPlacements.register(EntityTypeRegistry.SQUIRREL.get(), SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Squirrel::checkAnimalSpawnRules);
            });
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event){
            event.put(EntityTypeRegistry.SQUIRREL.get(), Squirrel.createAttributes().build());
        }

        @SubscribeEvent
        public static void gatherData(GatherDataEvent event){
            DataGenerator generator = event.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            generator.addProvider(event.includeServer(), LootTableGen.create(packOutput));
            generator.addProvider(event.includeServer(), new RecipeGen(packOutput));
            generator.addProvider(event.includeClient(), new BlockStateGen(packOutput, existingFileHelper));
            generator.addProvider(event.includeClient(), new ItemModelGen(packOutput, existingFileHelper));
        }
    }
}