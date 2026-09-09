package wraith.harvest_scythes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;

import static wraith.harvest_scythes.HarvestScythes.LOGGER;

@SuppressWarnings({"unused", "SameParameterValue"})
public class Config {

    private static final String CONFIG_PATH = "harvest_scythes/config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config INSTANCE = null;

    private final HashSet<String> scytheBlacklist = new HashSet<>();
    private final HashSet<String> macheteBlacklist = new HashSet<>();

    private Config() {
    }

    public static Config getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Config();
            INSTANCE.loadConfig();
        }
        return INSTANCE;
    }

    public boolean canScytheHarvest(ResourceLocation block) {
        return !scytheBlacklist.contains(block.toString());
    }

    public boolean canMacheteHarvest(ResourceLocation block) {
        return !macheteBlacklist.contains(block.toString());
    }

    private JsonObject getDefaults() {
        JsonObject defaultConfig = new JsonObject();

        JsonArray scytheBlacklisted = new JsonArray();
        scytheBlacklisted.add("minecraft:lily_pad");
        defaultConfig.add("scythe_blacklisted_blocks", scytheBlacklisted);

        defaultConfig.add("machete_blacklisted_blocks", new JsonArray());

        return defaultConfig;
    }

    private void setConfigData(JsonObject data) {
        scytheBlacklist.clear();
        for (JsonElement element : data.getAsJsonArray("scythe_blacklisted_blocks")) {
            scytheBlacklist.add(element.getAsString());
        }
        macheteBlacklist.clear();
        for (JsonElement element : data.getAsJsonArray("machete_blacklisted_blocks")) {
            macheteBlacklist.add(element.getAsString());
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean loadConfig() {
        File file = FMLPaths.CONFIGDIR.get().resolve(CONFIG_PATH).toFile();
        if (!file.exists()) {
            setConfigData(getDefaults());
            createFile(getDefaults());
            return true;
        }
        try {
            JsonObject fileConfig = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonObject();
            JsonObject defaults = getDefaults();
            boolean changed = false;
            JsonObject merged = new JsonObject();
            for (String key : defaults.keySet()) {
                if (fileConfig.has(key)) {
                    merged.add(key, fileConfig.get(key));
                } else {
                    merged.add(key, defaults.get(key));
                    changed = true;
                }
            }
            setConfigData(merged);
            if (changed) {
                createFile(merged);
            }
            return true;
        } catch (Exception e) {
            LOGGER.info("Found error with config. Using default config.");
            setConfigData(getDefaults());
            createFile(getDefaults());
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFile(JsonObject contents) {
        File file = FMLPaths.CONFIGDIR.get().resolve(CONFIG_PATH).toFile();
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(contents));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
