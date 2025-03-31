package com.myEconomySystemSupport.registry;

import com.myEconomySystemSupport.MyEconomySystemSupport;
import com.myEconomySystemSupport.itmes.RandomItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item RANDOM_ITEM = new RandomItem(new Item.Settings().maxCount(1));

    public static void registerItems() {
        Registry.register(Registries.ITEM, Identifier.of(MyEconomySystemSupport.MOD_ID, "random_item"), RANDOM_ITEM);
    }
}
