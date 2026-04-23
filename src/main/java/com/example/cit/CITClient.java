package com.example.cit;

import com.example.cit.model.CITManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import com.example.cit.model.CITRule;

public class CITClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(CITManager.INSTANCE);

        ModelLoadingPlugin.register(pluginContext -> {
            for (CITRule rule : CITManager.INSTANCE.getRules()) {
                if (rule.replacementModel() != null) {
                    pluginContext.addModels(rule.replacementModel());
                }
            }
        });
    }
}
