package ru.itskekoff.protocol.data.playerlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itskekoff.protocol.data.GameProfile;
import ru.itskekoff.protocol.data.Gamemode;

@Data
@AllArgsConstructor
public class PlayerListEntry {
    private GameProfile profile;
    private Gamemode gameMode;
    private int ping;
    private String displayName;

    public PlayerListEntry(final GameProfile profile, final Gamemode gameMode) {
        this.profile = profile;
        this.gameMode = gameMode;
    }

    public PlayerListEntry(final GameProfile profile, final int ping) {
        this.profile = profile;
        this.ping = ping;
    }

    public PlayerListEntry(final GameProfile profile, final String displayName) {
        this.profile = profile;
        this.displayName = displayName;
    }

    public PlayerListEntry(final GameProfile profile) {
        this.profile = profile;
    }
}

