package net.minecraft.client.multiplayer;

import javax.annotation.Nullable;

import baritone.utils.accessor.IChunkProviderClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.MoreObjects;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderClient implements IChunkProvider, IChunkProviderClient {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The completely empty chunk used by ChunkProviderClient when chunkMapping doesn't contain the requested coordinates.
	 */
	private final Chunk blankChunk;
	private final Long2ObjectMap<Chunk> loadedChunks = new Long2ObjectOpenHashMap<Chunk>(8192) {
		protected void rehash(int p_rehash_1_) {
			if (p_rehash_1_ > this.key.length) {
				super.rehash(p_rehash_1_);
			}
		}
	};

	/** Reference to the World object. */
	private final World world;

	public ChunkProviderClient(World worldIn) {
		this.blankChunk = new EmptyChunk(worldIn, 0, 0);
		this.world = worldIn;
	}

	/**
	 * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set to false
	 */
	public void unloadChunk(int x, int z) {
		Chunk chunk = this.provideChunk(x, z);

		if (!chunk.isEmpty()) {
			chunk.onUnload();
		}

		this.loadedChunks.remove(ChunkPos.asLong(x, z));
	}

	@Nullable
	public Chunk getLoadedChunk(int x, int z) {
		return (Chunk) this.loadedChunks.get(ChunkPos.asLong(x, z));
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int chunkX, int chunkZ) {
		Chunk chunk = new Chunk(this.world, chunkX, chunkZ);
		this.loadedChunks.put(ChunkPos.asLong(chunkX, chunkZ), chunk);
		chunk.markLoaded(true);
		return chunk;
	}

	public Chunk provideChunk(int x, int z) {
		return (Chunk) MoreObjects.firstNonNull(this.getLoadedChunk(x, z), this.blankChunk);
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean tick() {
		long i = System.currentTimeMillis();
		ObjectIterator objectiterator = this.loadedChunks.values().iterator();

		while (objectiterator.hasNext()) {
			Chunk chunk = (Chunk) objectiterator.next();
			chunk.onTick(System.currentTimeMillis() - i > 5L);
		}

		if (System.currentTimeMillis() - i > 100L) {
			LOGGER.info("Warning: Clientside chunk ticking took {} ms", (long) (System.currentTimeMillis() - i));
		}

		return false;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString() {
		return "MultiplayerChunkCache: " + this.loadedChunks.size() + ", " + this.loadedChunks.size();
	}

	public boolean isChunkGeneratedAt(int x, int z) {
		return this.loadedChunks.containsKey(ChunkPos.asLong(x, z));
	}

	@Override
	public Long2ObjectMap<Chunk> loadedChunks() {
		return this.loadedChunks;
	}
}
