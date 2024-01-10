package ru.itskekoff.client.module;

import com.google.gson.JsonObject;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.src.Config;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.clickgui.Configurable;
import ru.itskekoff.client.clickgui.settings.Setting;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.event.EventManager;
import ru.itskekoff.utils.notification.NotificationRenderer;

public @Data class Module extends Configurable {
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static SolevoyClient client = SolevoyClient.getInstance();
    protected static ModuleManager moduleManager = client.getModuleManager();
    protected static FontRenderer fr = mc.fontRenderer;
    public boolean visible = true;
    private String name, description;
    private int key;
    private Category category;
    private boolean toggled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.key = 0;
        this.category = category;
        this.toggled = false;
    }


    public void delKey() {
        this.key = 0;
    }


    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (this.toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            if (client.getModuleManager().getModule("Notification").isToggled()) {
                NotificationRenderer.queue(name, "Enabled", 2);
            }
            this.onEnable();
        } else {
            if (client.getModuleManager().getModule("Notification").isToggled()) {
                NotificationRenderer.queue(name, "Disabled", 2);
            }
            this.onDisable();
        }
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("state", isToggled());
        object.addProperty("keyIndex", getKey());
        object.addProperty("visible", isVisible());
        JsonObject propertiesObject = new JsonObject();
        for (Setting set : this.getSettings()) {
            if (this.getSettings() != null) {
                if (set instanceof BooleanSetting) {
                    propertiesObject.addProperty(set.getName(), ((BooleanSetting) set).isToggled());
                } else if (set instanceof ListSetting) {
                    propertiesObject.addProperty(set.getName(), ((ListSetting) set).getCurrentMode());
                } else if (set instanceof SliderSetting) {
                    propertiesObject.addProperty(set.getName(), ((SliderSetting) set).getCurrent());
                } else if (set instanceof StringSetting) {
                    propertiesObject.addProperty(set.getName(), ((StringSetting) set).getCurrentText());
                }
            }
            object.add("Settings", propertiesObject);
        }
        return object;
    }

    public void load(JsonObject object) {
        if (object != null) {
            if (object.has("state")) {
                this.setToggled(object.get("state").getAsBoolean());
            }
            if (object.has("visible")) {
                this.setVisible(object.get("visible").getAsBoolean());
            }
            if (object.has("keyIndex")) {
                this.setKey(object.get("keyIndex").getAsInt());
            }
            for (Setting set : getSettings()) {
                JsonObject propertiesObject = object.getAsJsonObject("Settings");
                if (set == null)
                    continue;
                if (propertiesObject == null)
                    continue;
                if (!propertiesObject.has(set.getName()))
                    continue;
                if (set instanceof BooleanSetting) {
                    ((BooleanSetting) set).setToggled(propertiesObject.get(set.getName()).getAsBoolean());
                } else if (set instanceof ListSetting) {
                    ((ListSetting) set).setCurrentMode(propertiesObject.get(set.getName()).getAsString());
                } else if (set instanceof SliderSetting) {
                    ((SliderSetting) set).setCurrent(propertiesObject.get(set.getName()).getAsFloat());
                } else if (set instanceof StringSetting) {
                    ((StringSetting) set).setCurrentText(propertiesObject.get(set.getName()).getAsString());
                }
            }
        }
    }

    public void onEnable() {
        EventManager.register(this);
    }

    public void onDisable() {
        EventManager.unregister(this);
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return category;
    }

}