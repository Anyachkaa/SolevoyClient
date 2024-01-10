package ru.itskekoff.protocol.data.status;

import lombok.Data;
import net.minecraft.util.text.ITextComponent;

@Data
public class ServerStatusInfo {
    private final VersionInfo versionInfo;
    private final PlayerInfo playerInfo;
    private final ITextComponent description;
    private final String icon;
}