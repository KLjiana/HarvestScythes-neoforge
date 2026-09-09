package wraith.harvest_scythes;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wraith.harvest_scythes.api.scythe.HSScythesEvents;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.registry.EnchantsRegistry;
import wraith.harvest_scythes.registry.ItemRegistry;
import wraith.harvest_scythes.support.GobberSupport;
import wraith.harvest_scythes.util.Config;

@Mod(HarvestScythes.MOD_ID)
@Mod.EventBusSubscriber(modid = HarvestScythes.MOD_ID)
public class HarvestScythes {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "harvest_scythes";
    private static boolean loaded = false;

    public HarvestScythes() {
        ItemRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        EnchantsRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        load();
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        LOGGER.info("Loading [Harvest Scythes]");

        ItemRegistry.init();
        EnchantsRegistry.registerEnchantments();

//        if (ModList.get().isLoaded("dragonloot")) {
//            LOGGER.info("[DragonLoot] detected. Loading supported items.");
//            DragonLootSupport.loadItems();
//        }
        if (ModList.get().isLoaded("gobber2")) {
            LOGGER.info("[Gobber] detected. Loading supported items.");
            GobberSupport.loadItems();
        }
        if (ModList.get().isLoaded("pigsteel")) {
            LOGGER.info("[PigSteel] detected. Loading supported recipes.");
        }
        Config.getInstance();
        HSScythesEvents.addSingleHarvestListener(event -> {
            var state = event.blockState();
            if (!(state.getBlock() instanceof CropBlock) || EnchantmentHelper.getItemEnchantmentLevel(EnchantsRegistry.get("blind_harvest_curse"), event.stack()) > 0) {
                return;
            }
            SoundType sound = state.getSoundType();
            event.world().playSound(null, event.cropPos(), sound.getBreakSound(), SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
        });

        LOGGER.info("[Harvest Scythes] has successfully been loaded!");
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (MacheteItem.tryHarvest(level, event.getPlayer(), event.getPos(), event.getState())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("harvest_scythes")
            .then(Commands.literal("reload")
                .requires(source -> source.hasPermission(1))
                .executes(context -> {
                    Config.getInstance().loadConfig();
                    context.getSource().sendSuccess(() -> Component.literal("§6[§eHarvest Scythes§6] §3has successfully reloaded!"), false);
                    return 1;
                })
            )
        );
    }

}
