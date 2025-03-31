package com.myEconomySystemSupport;

import com.myEconomySystemSupport.registry.ModItems;
import com.myEconomySystemSupport.registry.RandomItemPool;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyEconomySystemSupport implements ModInitializer {
    public static final String MOD_ID = "myeconomysystemsupport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // 아이템 등록
        ModItems.registerItems();

        // 아이템 풀 초기화
        RandomItemPool.init();

        // 아이템을 창조 인벤토리 탭에 추가
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ModItems.RANDOM_ITEM);
        });

        LOGGER.info("Random Item Mod initialized!");
    }
}
