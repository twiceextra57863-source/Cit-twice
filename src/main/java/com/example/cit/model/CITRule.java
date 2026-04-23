package com.example.cit.model;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public record CITRule(
    String packPath,
    List<Item> items,
    Identifier replacementModel,
    Map<Identifier, Identifier> textures,
    Map<String, Pattern> componentPatterns,
    Pattern displayNamePattern,
    IntRange damageRange,
    IntRange stackSizeRange,
    Map<Identifier, IntRange> enchantmentLevels,
    int weight
) {
    public boolean matches(ItemStack stack) {
        if (items != null && !items.isEmpty() && !items.contains(stack.getItem())) {
            return false;
        }

        if (displayNamePattern != null) {
            Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
            String name = (customName != null) ? customName.getString() : stack.getName().getString();
            if (!displayNamePattern.matcher(name).matches()) {
                return false;
            }
        }

        if (damageRange != null) {
            int damage = stack.getDamage();
            if (!damageRange.contains(damage)) return false;
        }

        if (stackSizeRange != null) {
            if (!stackSizeRange.contains(stack.getCount())) return false;
        }

        if (enchantmentLevels != null && !enchantmentLevels.isEmpty()) {
            ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
            if (enchantments == null) return false;

            for (Map.Entry<Identifier, IntRange> entry : enchantmentLevels.entrySet()) {
                boolean found = false;
                for (RegistryEntry<Enchantment> ench : enchantments.getEnchantments()) {
                    if (ench.getKey().isPresent() && ench.getKey().get().getValue().equals(entry.getKey())) {
                        if (entry.getValue().contains(enchantments.getLevel(ench))) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) return false;
            }
        }

        return true;
    }

    public record IntRange(int min, int max) {
        public boolean contains(int value) {
            return value >= min && value <= max;
        }
    }
}
