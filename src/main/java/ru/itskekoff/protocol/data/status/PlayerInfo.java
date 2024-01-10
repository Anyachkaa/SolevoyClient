package ru.itskekoff.protocol.data.status;

import lombok.Data;
import ru.itskekoff.protocol.data.GameProfile;

@Data
public class PlayerInfo {
    private final int onlinePlayers, maxPlayers;
    private final GameProfile[] players;
}