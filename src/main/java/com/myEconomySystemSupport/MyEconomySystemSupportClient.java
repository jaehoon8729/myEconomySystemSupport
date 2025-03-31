package com.myEconomySystemSupport;

import com.myEconomySystemSupport.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class MyEconomySystemSupportClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 클라이언트 모드 초기화
        System.out.println("MyEconomySystemSupport 클라이언트가 초기화되었습니다.");

        // 골드 아이템을 도구 아이템 그룹에 추가 (아이템이 인벤토리에 표시되도록)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(ModItems.RANDOM_ITEM);
        });
    }
}
