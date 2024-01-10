package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;

import baritone.Baritone;
import com.github.steveice10.mc.protocol.data.game.world.map.MapIcon;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.optifine.SmartAnimations;
import org.lwjgl.opengl.GL14;
import ru.itskekoff.event.EventManager;
import ru.itskekoff.event.impl.EventRenderChunkContainer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public abstract class ChunkRenderContainer {
	private double viewEntityX;
	private double viewEntityY;
	private double viewEntityZ;
	protected List<RenderChunk> renderChunks = Lists.<RenderChunk>newArrayListWithCapacity(17424);
	protected boolean initialized;
	private BitSet animatedSpritesRendered;
	private final BitSet animatedSpritesCached = new BitSet();

	public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
		this.initialized = true;
		this.renderChunks.clear();
		this.viewEntityX = viewEntityXIn;
		this.viewEntityY = viewEntityYIn;
		this.viewEntityZ = viewEntityZIn;

		if (SmartAnimations.isActive()) {
			if (this.animatedSpritesRendered != null) {
				SmartAnimations.spritesRendered(this.animatedSpritesRendered);
			} else {
				this.animatedSpritesRendered = this.animatedSpritesCached;
			}

			this.animatedSpritesRendered.clear();
		} else if (this.animatedSpritesRendered != null) {
			SmartAnimations.spritesRendered(this.animatedSpritesRendered);
			this.animatedSpritesRendered = null;
		}
	}

	public void preRenderChunk(RenderChunk renderChunkIn) {
		EventRenderChunkContainer event = new EventRenderChunkContainer(renderChunkIn);
		EventManager.call(event);
		if (Baritone.settings().renderCachedChunks.value && !Minecraft.getInstance().isSingleplayer() && Minecraft.getInstance().world.getChunk(renderChunkIn.getPosition()).isEmpty()) {
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GL14.glBlendColor(0, 0, 0, Baritone.settings().cachedChunksOpacity.value);
			GlStateManager.tryBlendFuncSeparate(GL_CONSTANT_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA, GL_ONE, GL_ZERO);
		}

		BlockPos blockpos = renderChunkIn.getPosition();
		GlStateManager.translate((float) ((double) blockpos.getX() - this.viewEntityX), (float) ((double) blockpos.getY() - this.viewEntityY), (float) ((double) blockpos.getZ() - this.viewEntityZ));
	}

	public void addRenderChunk(RenderChunk renderChunkIn, BlockRenderLayer layer) {
		this.renderChunks.add(renderChunkIn);

		if (this.animatedSpritesRendered != null) {
			BitSet bitset = renderChunkIn.compiledChunk.getAnimatedSprites(layer);

			if (bitset != null) {
				this.animatedSpritesRendered.or(bitset);
			}
		}
	}

	public abstract void renderChunkLayer(BlockRenderLayer layer);
}
