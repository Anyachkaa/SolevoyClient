package ru.itskekoff.client.configuration;

import java.io.File;

import lombok.Data;
import ru.itskekoff.client.SolevoyClient;

import com.google.gson.JsonObject;

public @Data class Config implements ConfigUpdater{

    private final String name;
    private final File file;

    private final SolevoyClient client = SolevoyClient.getInstance();

    public Config(String name) {
        this.name = name;
        this.file = new File(ConfigManager.configDirectory, name + ".json");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
        }
    }

    public JsonObject save() {
        JsonObject jsonObject = new JsonObject();
        JsonObject modulesObject = new JsonObject();
        client.getModuleManager().modules.forEach(module -> {
            modulesObject.add(module.getName(), module.save());
        });
        jsonObject.add("Modules", modulesObject);
        return jsonObject;
    }

    @Override
    public void load(JsonObject object) {
        if (object.has("Modules")) {
            JsonObject modulesObject = object.getAsJsonObject("Modules");
            client.getModuleManager().modules.forEach(module -> {
                module.setToggled(false);
                module.load(modulesObject.getAsJsonObject(module.getName()));
            });
        }
    }


}
