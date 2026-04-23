package com.example.cit.model;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import com.example.cit.CITMod;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CITManager implements SimpleResourceReloadListener<List<CITRule>> {
    public static final CITManager INSTANCE = new CITManager();
    private List<CITRule> rules = new ArrayList<>();

    private CITManager() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(CITMod.MOD_ID, "cit");
    }

    @Override
    public CompletableFuture<List<CITRule>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            List<CITRule> loadedRules = new ArrayList<>();
            manager.findResources("optifine/cit", path -> path.getPath().endsWith(".properties")).forEach((id, resource) -> {
                try (InputStream is = resource.getInputStream()) {
                    CITParser.parse(id.toString(), is).ifPresent(loadedRules::add);
                } catch (Exception e) {
                    CITMod.LOGGER.error("Failed to load CIT rule: " + id, e);
                }
            });
            loadedRules.sort(Comparator.comparingInt(CITRule::weight).reversed());
            return loadedRules;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(List<CITRule> loadedRules, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            this.rules = loadedRules;
            CITMod.LOGGER.info("Loaded " + rules.size() + " CIT rules");
        }, executor);
    }

    public List<CITRule> getRules() {
        return rules;
    }
}
