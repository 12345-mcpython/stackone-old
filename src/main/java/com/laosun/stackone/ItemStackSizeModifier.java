package com.laosun.stackone;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// written by chinese "doubao" ai
public class ItemStackSizeModifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemStackSizeModifier.class);

    @FunctionalInterface
    interface MaxStackSizeFieldGetter {
        Field get(Item item) throws NoSuchFieldException;
    }

    private static final MaxStackSizeFieldGetter defaultMaxStackSizeFieldGetter = item -> {
        try {
            return Item.class.getDeclaredField("maxStackSize");
        } catch (NoSuchFieldException e) {
            return null;
        }
    };

    private static final MaxStackSizeFieldGetter alternativeMaxStackSizeFieldGetter = item -> {
        try {
            return Item.class.getDeclaredField("f_41370_");
        } catch (NoSuchFieldException e) {
            return null;
        }
    };

    @SuppressWarnings("deprecation")
    public static void modifyItemStackSizes() {
        ArrayList<String> ignoreItems = IgnoreItem.getIgnoreItems();
        List<Item> itemsToModify = new ArrayList<>();

        for (Item i : ForgeRegistries.ITEMS) {
            if (!ignoreItems.contains(i.builtInRegistryHolder().key().location().toString())) {
                itemsToModify.add(i);
            }
        }

        List<MaxStackSizeFieldGetter> fieldGetterStrategies = List.of(defaultMaxStackSizeFieldGetter, alternativeMaxStackSizeFieldGetter);

        for (Item item : itemsToModify) {
            Field maxStackSizeField = null;
            for (MaxStackSizeFieldGetter strategy : fieldGetterStrategies) {
                try {
                    maxStackSizeField = strategy.get(item);
                    if (maxStackSizeField != null) {
                        break;
                    }
                } catch (NoSuchFieldException ignored) {
                }
            }

            if (maxStackSizeField == null) {
                LOGGER.error("Could not find valid maxStackSize field for item {}", item);
                continue;
            }

            maxStackSizeField.setAccessible(true);
            try {
                maxStackSizeField.set(item, 1);
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to set maxStackSize field for item {}", item, e);
            }
        }
    }
}