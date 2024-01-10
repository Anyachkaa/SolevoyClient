package ru.itskekoff.protocol.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Skin {
    private final GameProfile gameProfile;

    public String getValue() {
        return gameProfile.getProperty("textures").getValue();
    }

    public String getSignature() {
        return gameProfile.getProperty("textures").getSignature();
    }
}