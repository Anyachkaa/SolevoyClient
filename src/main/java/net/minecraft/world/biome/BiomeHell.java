package net.minecraft.world.biome;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;

public class BiomeHell extends Biome {
	public BiomeHell(BiomeProperties properties) {
		super(properties);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 50, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 2, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 4, 4));
		this.decorator = new BiomeHellDecorator();
	}
}
