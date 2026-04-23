package com.example.cit.mixin;

import com.example.cit.model.CITManager;
import com.example.cit.model.CITRule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
                    at = @At("HEAD"), argsOnly = true, index = 8)
    private BakedModel onRenderItem(BakedModel model, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return model;

        for (CITRule rule : CITManager.INSTANCE.getRules()) {
            if (rule.matches(stack)) {
                if (rule.replacementModel() != null) {
                    BakedModel replacement = MinecraftClient.getInstance().getBakedModelManager().getModel(rule.replacementModel());
                    if (replacement != null && replacement != MinecraftClient.getInstance().getBakedModelManager().getMissingModel()) {
                        return replacement;
                    }
                }
            }
        }
        return model;
    }
}
