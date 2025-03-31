package com.myEconomySystemSupport;

import com.myEconomySystemSupport.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyEconomySystemSupportClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyEconomySystemSupport.MOD_ID + "_client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("MyEconomySystemSupport 클라이언트 초기화 시작...");

        try {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
                content.add(ModItems.RANDOM_ITEM);
                LOGGER.info("클라이언트 측 아이템 그룹 등록 완료");
            });

            // 필요한 경우 클라이언트 측 렌더러 등록 코드를 여기에 추가할 수 있음

            LOGGER.info("MyEconomySystemSupport 클라이언트 초기화 완료!");
        } catch (Exception e) {
            LOGGER.error("클라이언트 초기화 중 오류 발생: ", e);
        }
    }
}