package com.myEconomySystemSupport.registry;

import com.myEconomySystemSupport.MyEconomySystemSupport;
import com.myEconomySystemSupport.itmes.RandomItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    // 레지스트리 키 먼저 생성
    public static final RegistryKey<Item> RANDOM_ITEM_KEY = RegistryKey.of(
            RegistryKeys.ITEM,
            Identifier.of(MyEconomySystemSupport.MOD_ID, "random_item")
    );

    // 아이템 설정에 레지스트리 키 포함
    public static final Item RANDOM_ITEM = new RandomItem(
            new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)
                    .registryKey(RANDOM_ITEM_KEY)
    );

    public static void registerItems() {
        Registry.register(Registries.ITEM, RANDOM_ITEM_KEY, RANDOM_ITEM);
    }
}