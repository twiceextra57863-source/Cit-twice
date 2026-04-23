package com.example.cit.model;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CITParser {
    public static Optional<CITRule> parse(String path, InputStream is) {
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            return Optional.empty();
        }

        String type = props.getProperty("type", "item");
        if (!type.equals("item")) return Optional.empty();

        List<Item> items = new ArrayList<>();
        String itemsProp = props.getProperty("items");
        if (itemsProp != null) {
            for (String s : itemsProp.split(" ")) {
                if (!s.contains(":")) s = "minecraft:" + s;
                Registries.ITEM.getOptionalValue(Identifier.of(s)).ifPresent(items::add);
            }
        }

        String modelProp = props.getProperty("model");
        Identifier modelId = null;
        if (modelProp != null) {
             modelId = Identifier.of(modelProp);
             if (modelId.getPath().endsWith(".json")) {
                 modelId = Identifier.of(modelId.getNamespace(), modelId.getPath().substring(0, modelId.getPath().length() - 5));
             }
             if (!modelId.getPath().contains("/")) {
                 modelId = Identifier.of(modelId.getNamespace(), "item/" + modelId.getPath());
             }
        }

        Map<Identifier, Identifier> textures = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("texture.")) {
                textures.put(Identifier.of(key.substring(8)), Identifier.of(props.getProperty(key)));
            }
        }

        Pattern namePattern = parsePattern(props.getProperty("nbt.display.Name"));
        CITRule.IntRange damageRange = parseIntRange(props.getProperty("damage"));
        CITRule.IntRange stackSizeRange = parseIntRange(props.getProperty("stackSize"));

        Map<Identifier, CITRule.IntRange> enchantments = new HashMap<>();
        String enchantmentIds = props.getProperty("enchantmentIDs");
        if (enchantmentIds != null) {
            String enchantmentLevels = props.getProperty("enchantmentLevels", "0-255");
            CITRule.IntRange levels = parseIntRange(enchantmentLevels);
            for (String s : enchantmentIds.split(" ")) {
                if (!s.contains(":")) s = "minecraft:" + s;
                enchantments.put(Identifier.of(s), levels);
            }
        }

        int weight = Integer.parseInt(props.getProperty("weight", "0"));

        return Optional.of(new CITRule(path, items, modelId, textures, new HashMap<>(), namePattern, damageRange, stackSizeRange, enchantments, weight));
    }

    private static Pattern parsePattern(String prop) {
        if (prop == null) return null;
        if (prop.startsWith("pattern:")) return Pattern.compile(wildcardToRegex(prop.substring(8)));
        if (prop.startsWith("regex:")) return Pattern.compile(prop.substring(6));
        return Pattern.compile(Pattern.quote(prop));
    }

    private static CITRule.IntRange parseIntRange(String prop) {
        if (prop == null) return null;
        try {
            if (prop.contains("-")) {
                String[] parts = prop.split("-");
                return new CITRule.IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
            int val = Integer.parseInt(prop);
            return new CITRule.IntRange(val, val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String wildcardToRegex(String wildcard) {
        return "^" + Pattern.quote(wildcard).replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q") + "$";
    }
}
