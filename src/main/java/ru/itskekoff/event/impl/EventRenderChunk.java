package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import ru.itskekoff.event.Event;


@AllArgsConstructor
public class EventRenderChunk extends Event {
    private @Getter @Setter BlockPos blockPos;
    private @Getter @Setter RenderChunk chunk;
}
