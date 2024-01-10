package ru.itskekoff.client.configuration;

import com.google.gson.JsonObject;

public interface ConfigUpdater {
    JsonObject save();

    void load(JsonObject object);
}
