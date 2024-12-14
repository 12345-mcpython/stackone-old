package com.laosun.stackone;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(StackOneMod.MODID)
public class StackOneMod {
    public static final String MODID = "stackone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public StackOneMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("StackOneMod loaded");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ItemStackSizeModifier.modifyItemStackSizes();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getStack();

        if (stack.getCount() > 1) {
            splitStacksInInventory(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getCrafting();

        if (stack.getCount() > 1) {
            splitStacksInInventory(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getSmelting();

        if (stack.getCount() > 1) {
            splitStacksInInventory(player);
        }
    }

    private void splitStacksInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.getCount() > 1) {
                int stackSize = stack.getCount();
                stack.setCount(1);

                for (int j = 1; j < stackSize; j++) {
                    ItemStack singleItemStack = stack.copy();

                    if (!player.getInventory().add(singleItemStack)) {
                        player.drop(singleItemStack, false);
                    }
                }
            }
        }
    }
}