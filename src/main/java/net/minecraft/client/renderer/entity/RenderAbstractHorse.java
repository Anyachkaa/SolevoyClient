package net.minecraft.client.renderer.entity;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.ResourceLocation;

public class RenderAbstractHorse extends RenderLiving<AbstractHorse> {
	private static final Map<Class<?>, ResourceLocation> MAP = Maps.<Class<?>, ResourceLocation>newHashMap();
	private final float scale;

	public RenderAbstractHorse(RenderManager manager) {
		this(manager, 1.0F);
	}

	public RenderAbstractHorse(RenderManager renderManagerIn, float scaleIn) {
		super(renderManagerIn, new ModelHorse(), 0.75F);
		this.scale = scaleIn;
	}

	/**
	 * Allows the render to do state modifications necessary before the model is rendered.
	 */
	protected void preRenderCallback(AbstractHorse entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(this.scale, this.scale, this.scale);
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(AbstractHorse entity) {
		return MAP.get(entity.getClass());
	}

	static {
		MAP.put(EntityDonkey.class, new ResourceLocation("textures/entity/horse/donkey.png"));
		MAP.put(EntityMule.class, new ResourceLocation("textures/entity/horse/mule.png"));
		MAP.put(EntityZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"));
		MAP.put(EntitySkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png"));
	}
}
