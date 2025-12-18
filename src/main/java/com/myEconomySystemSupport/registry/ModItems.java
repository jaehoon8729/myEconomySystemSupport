package com.myEconomySystemSupport.registry;

import com.myEconomySystemSupport.MyEconomySystemSupport;
import com.myEconomySystemSupport.itmes.RandomItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    // 아이템 생성 (1.21.1에서는 registryKey 불필요)
    public static final Item RANDOM_ITEM = new RandomItem(
            new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)
    );

    public static void registerItems() {
        Registry.register(Registries.ITEM, Identifier.of(MyEconomySystemSupport.MOD_ID, "random_item"), RANDOM_ITEM);
    }
}