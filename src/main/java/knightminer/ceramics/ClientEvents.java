package knightminer.ceramics;

import knightminer.ceramics.client.ClayBucketModel;
import knightminer.ceramics.client.gui.KilnScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid=Ceramics.MOD_ID, bus=Bus.MOD, value=Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  public static void setupClient(FMLClientSetupEvent event) {
    ScreenManager.registerFactory(Registration.KILN_CONTAINER.get(), KilnScreen::new);
  }

  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation("ceramics", "bucket"), ClayBucketModel.Loader.INSTANCE);
  }
}