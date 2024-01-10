package net.optifine.override;

import java.util.Arrays;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.utils.IPlayerContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayCache;

public class ChunkCacheOF implements IBlockAccess {
	private final ChunkCache chunkCache;
	private final int posX;
	private final int posY;
	private final int posZ;
	private final int sizeX;
	private final int sizeY;
	private final int sizeZ;
	private final int sizeXY;
	private int[] combinedLights;
	private IBlockState[] blockStates;
	private final int arraySize;
	private final boolean dynamicLights = Config.isDynamicLights();
	private static final ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
	private static final ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class, 16);

	public ChunkCacheOF(ChunkCache chunkCache, BlockPos posFromIn, BlockPos posToIn, int subIn) {
		this.chunkCache = chunkCache;
		int i = posFromIn.getX() - subIn >> 4;
		int j = posFromIn.getY() - subIn >> 4;
		int k = posFromIn.getZ() - subIn >> 4;
		int l = posToIn.getX() + subIn >> 4;
		int i1 = posToIn.getY() + subIn >> 4;
		int j1 = posToIn.getZ() + subIn >> 4;
		this.sizeX = l - i + 1 << 4;
		this.sizeY = i1 - j + 1 << 4;
		this.sizeZ = j1 - k + 1 << 4;
		this.sizeXY = this.sizeX * this.sizeY;
		this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
		this.posX = i << 4;
		this.posY = j << 4;
		this.posZ = k << 4;
	}

	private int getPositionIndex(BlockPos pos) {
		int i = pos.getX() - this.posX;

		if (i >= 0 && i < this.sizeX) {
			int j = pos.getY() - this.posY;

			if (j >= 0 && j < this.sizeY) {
				int k = pos.getZ() - this.posZ;
				return k >= 0 && k < this.sizeZ ? k * this.sizeXY + j * this.sizeX + i : -1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public int getCombinedLight(BlockPos pos, int lightValue) {
		int i = this.getPositionIndex(pos);

		if (i >= 0 && i < this.arraySize && this.combinedLights != null) {
			int j = this.combinedLights[i];

			if (j == -1) {
				j = this.getCombinedLightRaw(pos, lightValue);
				this.combinedLights[i] = j;
			}

			return j;
		} else {
			return this.getCombinedLightRaw(pos, lightValue);
		}
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return chunkCache.getBlockState(pos);
	}

	private int getCombinedLightRaw(BlockPos pos, int lightValue) {
		int i = this.chunkCache.getCombinedLight(pos, lightValue);

		if (this.dynamicLights && !this.getBlockState(pos).isOpaqueCube()) {
			i = DynamicLights.getCombinedLight(pos, i);
		}

		return i;
	}
	public void renderStart() {
		if (this.combinedLights == null) {
			this.combinedLights = (int[]) cacheCombinedLights.allocate(this.arraySize);
		}

		Arrays.fill(this.combinedLights, -1);

		if (this.blockStates == null) {
			this.blockStates = (IBlockState[]) cacheBlockStates.allocate(this.arraySize);
		}

		Arrays.fill(this.blockStates, (Object) null);
	}

	public void renderFinish() {
		cacheCombinedLights.free(this.combinedLights);
		this.combinedLights = null;
		cacheBlockStates.free(this.blockStates);
		this.blockStates = null;
	}

	public boolean isEmpty() {
		if (!chunkCache.isEmpty()) {
			return false;
		}
		if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
			Baritone baritone = (Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
			IPlayerContext ctx = baritone.getPlayerContext();
			if (ctx.player() != null && ctx.world() != null && baritone.bsi != null) {
				BlockPos position = ((RenderChunk) (Object) this).getPosition();
				// RenderChunk extends from -1,-1,-1 to +16,+16,+16
				// then the constructor of ChunkCache extends it one more (presumably to get things like the connected status of fences? idk)
				// so if ANY of the adjacent chunks are loaded, we are unempty
				for (int dx = -1; dx <= 1; dx++) {
					for (int dz = -1; dz <= 1; dz++) {
						if (baritone.bsi.isLoaded(16 * dx + position.getX(), 16 * dz + position.getZ())) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public Biome getBiome(BlockPos pos) {
		return this.chunkCache.getBiome(pos);
	}

	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return this.chunkCache.getStrongPower(pos, direction);
	}

	public TileEntity getTileEntity(BlockPos pos) {
		return this.chunkCache.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
	}

	public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType type) {
		return this.chunkCache.getTileEntity(pos, type);
	}

	public WorldType getWorldType() {
		return this.chunkCache.getWorldType();
	}

	/**
	 * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
	 */
	public boolean isAirBlock(BlockPos pos) {
		return this.chunkCache.isAirBlock(pos);
	}

	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return Reflector.callBoolean(this.chunkCache, Reflector.ForgeChunkCache_isSideSolid, pos, side, _default);
	}
}
