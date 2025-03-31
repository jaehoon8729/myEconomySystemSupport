package com.myEconomySystemSupport.itmes;

import com.myEconomySystemSupport.registry.RandomItemPool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        super(settings.rarity(Rarity.RARE));
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) return ActionResult.PASS;

        // 랜덤 아이템 생성
        ItemStack randomItemStack = RandomItemPool.getRandomItem();

        // 인벤토리에 아이템 추가
        if (!player.getInventory().insertStack(randomItemStack)) {
            player.dropItem(randomItemStack, false);
        }

        // 소리 효과 재생
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));

        // 메시지 출력
        player.sendMessage(Text.translatable("item.randomitem.random_item.success",
                randomItemStack.getCount(),
                randomItemStack.getName()), false);

        // 아이템을 소모
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return ActionResult.SUCCESS;
    }
}