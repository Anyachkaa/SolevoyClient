package ru.itskekoff.bots.chunks;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import lombok.Data;
import lombok.NonNull;
import ru.itskekoff.bots.Bot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public @Data class CachedChunk {
    private List<Bot> usages = new CopyOnWriteArrayList<>();
    private @NonNull Column chunk;
}