package com.myEconomySystemSupport.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myEconomySystemSupport.MyEconomySystemSupport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/myeconomysystemsupport/config.json");

    private static ModConfigData config;

    // 기본 설정값
    public static class ModConfigData {
        public boolean logItemResults = true;
        public boolean showPersonalMessages = true;
        public String personalMessageFormat = "§e[랜덤 아이템]§r %item% x%count%을(를) 획득했습니다!";
        public String broadcastMessageFormat = "§a[공지]§r §e%player%§r님이 %item% x%count%을(를) 획득했습니다!";
    }

    public static void init() {
        loadConfig();
    }

    public static void loadConfig() {
        try {
            // 설정 파일 디렉토리 확인 및 생성
            File configDir = new File("config/myeconomysystemsupport");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // 설정 파일 존재 확인
            if (CONFIG_FILE.exists()) {
                // 파일 읽기
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    config = GSON.fromJson(reader, ModConfigData.class);
                    MyEconomySystemSupport.LOGGER.info("설정 파일을 성공적으로 로드했습니다.");
                }
            } else {
                // 기본 설정 생성
                config = new ModConfigData();
                saveConfig();
                MyEconomySystemSupport.LOGGER.info("기본 설정 파일을 생성했습니다.");
            }
        } catch (Exception e) {
            MyEconomySystemSupport.LOGGER.error("설정 파일 로드 중 오류 발생", e);
            // 오류 발생 시 기본 설정 사용
            config = new ModConfigData();
        }
    }

    public static void saveConfig() {
        try {
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
            MyEconomySystemSupport.LOGGER.info("설정 파일을 저장했습니다.");
        } catch (IOException e) {
            MyEconomySystemSupport.LOGGER.error("설정 파일 저장 중 오류 발생", e);
        }
    }

    public static ModConfigData getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}