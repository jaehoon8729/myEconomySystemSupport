package com.myEconomySystemSupport.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myEconomySystemSupport.MyEconomySystemSupport;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomItemPool {
    private static final List<ItemEntry> ITEM_POOL = new ArrayList<>();
    // 아이템별 공지 여부를 저장하는 맵
    private static final Map<String, Boolean> BROADCAST_ITEMS = new HashMap<>();
    private static final Random random = new Random();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        // 리소스 리로드 리스너 등록
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of(MyEconomySystemSupport.MOD_ID, "item_pools");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        ITEM_POOL.clear();
                        BROADCAST_ITEMS.clear();

                        boolean defaultResourceFound = false;

                        // 모든 아이템 풀 파일 로드
                        for (Identifier id : manager.findResources("item_pools", path -> path.getPath().endsWith(".json")).keySet()) {
                            if (id.getPath().equals("item_pools/default.json")) {
                                defaultResourceFound = true;
                            }

                            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                                loadItemPoolFromJson(json);
                            } catch (Exception e) {
                                MyEconomySystemSupport.LOGGER.error("아이템 풀 파일 로드 중 오류 발생: " + id.toString(), e);
                            }
                        }

                        // 기본 아이템 풀 파일이 없으면 생성
                        if (!defaultResourceFound) {
                            createDefaultItemPoolFile();

                            // 기본 아이템 풀 로드
                            File defaultPoolFile = new File("config/" + MyEconomySystemSupport.MOD_ID, "default_item_pool.json");
                            if (defaultPoolFile.exists()) {
                                try {
                                    JsonObject json = JsonParser.parseReader(new InputStreamReader(
                                            new java.io.FileInputStream(defaultPoolFile), StandardCharsets.UTF_8)).getAsJsonObject();
                                    loadItemPoolFromJson(json);
                                } catch (Exception e) {
                                    MyEconomySystemSupport.LOGGER.error("기본 아이템 풀 파일 로드 중 오류 발생", e);
                                }
                            }
                        }

                        MyEconomySystemSupport.LOGGER.info("아이템 풀 로드 완료: " + ITEM_POOL.size() + "개의 아이템");
                    }
                }
        );
    }

    private static void createDefaultItemPoolFile() {
        try {
            File dataDir = new File("config/myeconomysystemsupport");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            File defaultPoolFile = new File(dataDir, "default_item_pool.json");
            if (!defaultPoolFile.exists()) {
                JsonArray itemsArray = new JsonArray();

                // 일반 아이템 (공지 없음)
                addDefaultItemToJson(itemsArray, "minecraft:diamond", 1, 3, 5, false);
                addDefaultItemToJson(itemsArray, "minecraft:iron_ingot", 1, 5, 15, false);
                addDefaultItemToJson(itemsArray, "minecraft:gold_ingot", 1, 3, 10, false);
                addDefaultItemToJson(itemsArray, "minecraft:emerald", 1, 2, 5, false);
                addDefaultItemToJson(itemsArray, "minecraft:ender_pearl", 1, 2, 8, false);
                addDefaultItemToJson(itemsArray, "minecraft:experience_bottle", 1, 3, 10, false);
                addDefaultItemToJson(itemsArray, "minecraft:cooked_beef", 1, 8, 20, false);
                addDefaultItemToJson(itemsArray, "minecraft:apple", 1, 3, 15, false);

                // 희귀 아이템 (전체 공지)
                addDefaultItemToJson(itemsArray, "minecraft:golden_apple", 1, 1, 3, true);
                addDefaultItemToJson(itemsArray, "minecraft:enchanted_golden_apple", 1, 1, 1, true);
                addDefaultItemToJson(itemsArray, "minecraft:netherite_ingot", 1, 1, 1, true);
                addDefaultItemToJson(itemsArray, "minecraft:diamond_sword", 1, 1, 3, true);
                addDefaultItemToJson(itemsArray, "minecraft:diamond_pickaxe", 1, 1, 3, true);

                // 일반 아이템
                addDefaultItemToJson(itemsArray, "minecraft:oak_log", 1, 16, 20, false);
                addDefaultItemToJson(itemsArray, "minecraft:stone", 1, 16, 25, false);

                JsonObject rootObject = new JsonObject();
                rootObject.add("items", itemsArray);

                try (Writer writer = new FileWriter(defaultPoolFile)) {
                    GSON.toJson(rootObject, writer);
                }

                MyEconomySystemSupport.LOGGER.info("기본 아이템 풀 파일 생성: " + defaultPoolFile.getAbsolutePath());
            }
        } catch (Exception e) {
            MyEconomySystemSupport.LOGGER.error("기본 아이템 풀 파일 생성 중 오류 발생", e);
        }
    }

    private static void addDefaultItemToJson(JsonArray array, String itemId, int minCount, int maxCount, int weight, boolean broadcastOnGet) {
        JsonObject itemObject = new JsonObject();
        itemObject.addProperty("item", itemId);
        itemObject.addProperty("min_count", minCount);
        itemObject.addProperty("max_count", maxCount);
        itemObject.addProperty("weight", weight);
        // "broadcast" 대신 "notice" 필드 사용
        itemObject.addProperty("notice", broadcastOnGet ? "true" : "false");
        array.add(itemObject);
    }

    private static void loadItemPoolFromJson(JsonObject json) {
        if (json.has("items") && json.get("items").isJsonArray()) {
            JsonArray itemsArray = json.getAsJsonArray("items");

            for (JsonElement element : itemsArray) {
                if (element.isJsonObject()) {
                    JsonObject itemObject = element.getAsJsonObject();
                    try {
                        String itemId = itemObject.get("item").getAsString();
                        int minCount = itemObject.get("min_count").getAsInt();
                        int maxCount = itemObject.get("max_count").getAsInt();
                        int weight = itemObject.get("weight").getAsInt();

                        // 공지 여부 속성 (기본값: false)
                        boolean broadcastOnGet = false;

                        // "notice" 필드 확인
                        if (itemObject.has("notice")) {
                            String noticeValue = itemObject.get("notice").getAsString();
                            broadcastOnGet = "true".equalsIgnoreCase(noticeValue);
                        }
                        // 이전 버전 호환성을 위해 "broadcast" 필드도 확인
                        else if (itemObject.has("broadcast")) {
                            String broadcastValue = itemObject.get("broadcast").getAsString();
                            broadcastOnGet = "Y".equalsIgnoreCase(broadcastValue);
                        }

                        Identifier identifier = Identifier.of(itemId);
                        Item item = Registries.ITEM.get(identifier);

                        if (item != Items.AIR) {
                            addItem(item, minCount, maxCount, weight, broadcastOnGet);
                            // 공지 여부를 맵에 저장
                            BROADCAST_ITEMS.put(itemId, broadcastOnGet);
                        } else {
                            MyEconomySystemSupport.LOGGER.warn("아이템을 찾을 수 없음: " + itemId);
                        }
                    } catch (Exception e) {
                        MyEconomySystemSupport.LOGGER.error("잘못된 아이템 엔트리: " + itemObject, e);
                    }
                }
            }
        }
    }

    /**
     * 아이템 풀에 새 아이템 추가
     * @param item 추가할 아이템
     * @param minCount 최소 개수
     * @param maxCount 최대 개수
     * @param weight 가중치 (높을수록 더 자주 등장)
     * @param broadcastOnGet 획득 시 전체 공지 여부
     */
    public static void addItem(Item item, int minCount, int maxCount, int weight, boolean broadcastOnGet) {
        ITEM_POOL.add(new ItemEntry(item, minCount, maxCount, weight, broadcastOnGet));
    }

    /**
     * 랜덤 아이템 생성
     * @return 랜덤으로 선택된 아이템스택
     */
    public static ItemStack getRandomItem() {
        if (ITEM_POOL.isEmpty()) {
            // 아이템 풀이 비어있으면 기본값 반환
            return new ItemStack(Items.APPLE);
        }

        // 가중치 총합 계산
        int totalWeight = ITEM_POOL.stream().mapToInt(entry -> entry.weight).sum();

        // 가중치 기반 랜덤 선택
        int randWeight = random.nextInt(totalWeight);
        int weightSum = 0;

        for (ItemEntry entry : ITEM_POOL) {
            weightSum += entry.weight;
            if (randWeight < weightSum) {
                // 개수 랜덤 결정
                int count = entry.minCount;
                if (entry.maxCount > entry.minCount) {
                    count += random.nextInt(entry.maxCount - entry.minCount + 1);
                }

                // 기본 아이템스택 생성
                ItemStack result = new ItemStack(entry.item, count);

                return result;
            }
        }

        // 기본값 (오류 방지용)
        return new ItemStack(Items.APPLE);
    }

    /**
     * 아이템이 전체 공지 대상인지 확인
     * @param item 확인할 아이템
     * @return 전체 공지 대상이면 true
     */
    public static boolean shouldBroadcast(ItemStack item) {
        String itemId = Registries.ITEM.getId(item.getItem()).toString();
        return BROADCAST_ITEMS.getOrDefault(itemId, false);
    }

    // 아이템 엔트리 클래스
    private static class ItemEntry {
        final Item item;
        final int minCount;
        final int maxCount;
        final int weight;
        final boolean broadcastOnGet;

        ItemEntry(Item item, int minCount, int maxCount, int weight, boolean broadcastOnGet) {
            this.item = item;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.weight = weight;
            this.broadcastOnGet = broadcastOnGet;
        }
    }
}