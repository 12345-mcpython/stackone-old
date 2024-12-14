package com.laosun.stackone;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;


public class IgnoreItem {
    public static final Logger LOGGER = LogUtils.getLogger();
    public String item;

    public static ArrayList<String> getIgnoreItems() {
        File dir = new File("config/stackone");
        if (!dir.exists() && dir.mkdirs()) {
            LOGGER.info("Fail to create config directory.");
        }
        File file = new File(dir, "ignore_item.json");
        Gson gson = new Gson();
        ArrayList<String> ignoreItems = new ArrayList<>();
        try {
            if (!file.exists() && !file.createNewFile()) {
                LOGGER.info("Fail to create config file.");
            } else {
                Files.writeString(file.toPath(), "[]", StandardCharsets.UTF_8);
            }
            String jsonString = Files.readString(file.toPath());
            JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
            for (JsonElement user : jsonArray) {
                IgnoreItem ignore = gson.fromJson(user, IgnoreItem.class);
                ignoreItems.add(ignore.item);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ignoreItems;
    }
}
