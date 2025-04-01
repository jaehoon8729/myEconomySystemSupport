package com.myEconomySystemSupport.itmes;

import com.myEconomySystemSupport.MyEconomySystemSupport;
import com.myEconomySystemSupport.config.ModConfig;
import com.myEconomySystemSupport.registry.RandomItemPool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class RandomItem extends Item {
    private static final Random random = new Random();

    public RandomItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // 클라이언트 측에서는 처리하지 않음
        if (world.isClient) return ActionResult.PASS;

        // 랜덤 아이템 생성
        ItemStack randomItemStack = RandomItemPool.getRandomItem();

        // 아이템 정보 가져오기
        String itemName = randomItemStack.getName().getString();
        int itemCount = randomItemStack.getCount();
        String playerName = player.getName().getString();

        // 아이템의 공지 여부 확인
        boolean shouldBroadcast = RandomItemPool.shouldBroadcast(randomItemStack);

        // 로그에 받은 아이템 정보 기록
        if (ModConfig.getConfig().logItemResults) {
            MyEconomySystemSupport.LOGGER.info("플레이어 {} 가 랜덤 아이템 사용: {} x{}를 받음 (공지여부: {})",
                    playerName, itemName, itemCount, shouldBroadcast ? "Y" : "N");
        }

        // 인벤토리에 아이템 추가
        if (!player.getInventory().insertStack(randomItemStack)) {
            player.dropItem(randomItemStack, false);
        }

        // 소리 효과 재생 (희귀 아이템이면 특별한 소리)
        if (shouldBroadcast) {
            // 희귀 아이템 획득 소리
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,
                    0.7f, 1.0f);
        } else {
            // 일반 아이템 획득 소리
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                    0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        }

        // 개인 메시지 출력
        if (ModConfig.getConfig().showPersonalMessages) {
            String personalMsg = ModConfig.getConfig().personalMessageFormat;

            // NullPointerException 방지를 위한 기본값 설정
            if (personalMsg == null || personalMsg.isEmpty()) {
                personalMsg = "§e[랜덤 아이템]§r %item% x%count%을(를) 획득했습니다!";
            }

            personalMsg = personalMsg
                    .replace("%item%", itemName)
                    .replace("%count%", String.valueOf(itemCount))
                    .replace("%player%", playerName);

            player.sendMessage(Text.of(personalMsg), false);
        }

        // 전체 채팅으로 공지 (아이템이 공지 대상으로 설정되어 있을 경우)
        if (shouldBroadcast && player instanceof ServerPlayerEntity) {
            MinecraftServer server = ((ServerPlayerEntity) player).getServer();
            if (server != null) {
                String broadcastMsg = ModConfig.getConfig().broadcastMessageFormat;

                // NullPointerException 방지를 위한 기본값 설정
                if (broadcastMsg == null || broadcastMsg.isEmpty()) {
                    broadcastMsg = "§a[공지]§r §e%player%§r님이 %item% x%count%을(를) 획득했습니다!";
                }

                broadcastMsg = broadcastMsg
                        .replace("%item%", itemName)
                        .replace("%count%", String.valueOf(itemCount))
                        .replace("%player%", playerName);

                server.getPlayerManager().broadcast(Text.of(broadcastMsg), false);

                // 로그에도 기록
                MyEconomySystemSupport.LOGGER.info("전체 공지: {}", broadcastMsg.replaceAll("§[0-9a-fklmnor]", ""));
            }
        }

        return ActionResult.SUCCESS;
    }
}