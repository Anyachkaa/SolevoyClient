package ru.itskekoff.client.module.impl.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventRenderChunk;
import ru.itskekoff.event.impl.EventRenderChunkContainer;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChunkAnimator extends Module {

    private final WeakHashMap<RenderChunk, AtomicLong> lifespans = new WeakHashMap<>();


    private SliderSetting delaySetting;

    public ChunkAnimator() {
        super("ChunkAnimator", "Анимирует чанки", Category.Render);
        delaySetting = new SliderSetting("Delay", "Animate delay", 1000, 0, 1000, 1);
        addSettings(delaySetting);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void preRender(EventRenderChunkContainer event) {
        RenderChunk chunk = event.getChunk();
        int delay = (int) delaySetting.getCurrent();
        render(chunk, delay);
    }

    private void render(RenderChunk chunk, int delay) {
        if (lifespans.containsKey(chunk)) {
            AtomicLong timeAlive = lifespans.get(chunk);
            long timeClone = timeAlive.get();
            if (timeClone == -1L) {
                timeClone = System.currentTimeMillis();
                timeAlive.set(timeClone);
            }

            long timeDifference = System.currentTimeMillis() - timeClone;
            if (timeDifference <= delay) {
                double chunkY = chunk.getPosition().getY();
                double offsetY = chunkY / delay * timeDifference;
                GlStateManager.translate(0.0, -chunkY + offsetY, 0.0);
            }
        }
    }

    @EventTarget
    public void setPosition(EventRenderChunk event) {
        if (mc.player != null) {
            if (!lifespans.containsKey(event.getChunk())) {
                lifespans.put(event.getChunk(), new AtomicLong(-1L));
            }
        }

    }
}
