package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.chunk.RenderChunk;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventRenderChunkContainer extends Event {
    private @Getter @Setter RenderChunk chunk;
}
