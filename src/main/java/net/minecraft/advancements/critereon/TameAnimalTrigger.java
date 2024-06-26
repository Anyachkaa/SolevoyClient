package net.minecraft.advancements.critereon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger implements ICriterionTrigger<TameAnimalTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("tame_animal");
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.<PlayerAdvancements, Listeners>newHashMap();

	public ResourceLocation getId() {
		return ID;
	}

	public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (tameanimaltrigger$listeners == null) {
			tameanimaltrigger$listeners = new Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, tameanimaltrigger$listeners);
		}

		tameanimaltrigger$listeners.add(listener);
	}

	public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (tameanimaltrigger$listeners != null) {
			tameanimaltrigger$listeners.remove(listener);

			if (tameanimaltrigger$listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("entity"));
		return new Instance(entitypredicate);
	}

	public void trigger(EntityPlayerMP player, EntityAnimal entity) {
		Listeners tameanimaltrigger$listeners = this.listeners.get(player.getAdvancements());

		if (tameanimaltrigger$listeners != null) {
			tameanimaltrigger$listeners.trigger(player, entity);
		}
	}

	public static class Instance extends AbstractCriterionInstance {
		private final EntityPredicate entity;

		public Instance(EntityPredicate entity) {
			super(TameAnimalTrigger.ID);
			this.entity = entity;
		}

		public boolean test(EntityPlayerMP player, EntityAnimal entity) {
			return this.entity.test(player, entity);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<Instance>> listeners = Sets.<Listener<Instance>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(Listener<Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(Listener<Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(EntityPlayerMP player, EntityAnimal entity) {
			List<Listener<Instance>> list = null;

			for (Listener<Instance> listener : this.listeners) {
				if (((Instance) listener.getCriterionInstance()).test(player, entity)) {
					if (list == null) {
						list = Lists.<Listener<Instance>>newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (Listener<Instance> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}
