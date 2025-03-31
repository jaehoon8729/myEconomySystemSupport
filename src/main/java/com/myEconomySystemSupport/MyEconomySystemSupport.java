package com.myEconomySystemSupport;

import com.myEconomySystemSupport.registry.ModItems;
import com.myEconomySystemSupport.registry.RandomItemPool;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyEconomySystemSupport implements ModInitializer {
    // 모든 ID는 소문자로만 구성
    public static final String MOD_ID = "myeconomysystemsupport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        try {
            // 아이템 등록
            ModItems.registerItems();
            LOGGER.info("아이템 등록 완료: {}", ModItems.RANDOM_ITEM);

            // 아이템 풀 초기화
            RandomItemPool.init();
            LOGGER.info("랜덤 아이템 풀 초기화 완료");

            // 아이템을 창조 인벤토리 탭에 추가
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
                entries.add(ModItems.RANDOM_ITEM);
            });
            LOGGER.info("아이템 그룹 등록 완료");

            LOGGER.info("Random Item Mod 초기화 완료!");
        } catch (Exception e) {
            LOGGER.error("모드 초기화 중 오류 발생: ", e);
        }
    }
}