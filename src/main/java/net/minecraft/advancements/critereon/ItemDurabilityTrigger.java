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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger implements ICriterionTrigger<ItemDurabilityTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.<PlayerAdvancements, Listeners>newHashMap();

	public ResourceLocation getId() {
		return ID;
	}

	public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		Listeners itemdurabilitytrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (itemdurabilitytrigger$listeners == null) {
			itemdurabilitytrigger$listeners = new Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, itemdurabilitytrigger$listeners);
		}

		itemdurabilitytrigger$listeners.add(listener);
	}

	public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		Listeners itemdurabilitytrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (itemdurabilitytrigger$listeners != null) {
			itemdurabilitytrigger$listeners.remove(listener);

			if (itemdurabilitytrigger$listeners.isEmpty()) {
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
		ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
		MinMaxBounds minmaxbounds = MinMaxBounds.deserialize(json.get("durability"));
		MinMaxBounds minmaxbounds1 = MinMaxBounds.deserialize(json.get("delta"));
		return new Instance(itempredicate, minmaxbounds, minmaxbounds1);
	}

	public void trigger(EntityPlayerMP player, ItemStack itemIn, int newDurability) {
		Listeners itemdurabilitytrigger$listeners = this.listeners.get(player.getAdvancements());

		if (itemdurabilitytrigger$listeners != null) {
			itemdurabilitytrigger$listeners.trigger(itemIn, newDurability);
		}
	}

	public static class Instance extends AbstractCriterionInstance {
		private final ItemPredicate item;
		private final MinMaxBounds durability;
		private final MinMaxBounds delta;

		public Instance(ItemPredicate item, MinMaxBounds durability, MinMaxBounds delta) {
			super(ItemDurabilityTrigger.ID);
			this.item = item;
			this.durability = durability;
			this.delta = delta;
		}

		public boolean test(ItemStack item, int p_193197_2_) {
			if (!this.item.test(item)) {
				return false;
			} else if (!this.durability.test((float) (item.getMaxDamage() - p_193197_2_))) {
				return false;
			} else {
				return this.delta.test((float) (item.getItemDamage() - p_193197_2_));
			}
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

		public void trigger(ItemStack item, int newDurability) {
			List<Listener<Instance>> list = null;

			for (Listener<Instance> listener : this.listeners) {
				if (((Instance) listener.getCriterionInstance()).test(item, newDurability)) {
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
