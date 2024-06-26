package net.minecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
	/** The bounding box for the pressure plate pressed state */
	protected static final AxisAlignedBB PRESSED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.03125D, 0.9375D);
	protected static final AxisAlignedBB UNPRESSED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

	/**
	 * This bounding box is used to check for entities in a certain area and then determine the pressed state.
	 */
	protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

	protected BlockBasePressurePlate(Material materialIn) {
		this(materialIn, materialIn.getMaterialMapColor());
	}

	protected BlockBasePressurePlate(Material materialIn, MapColor mapColorIn) {
		super(materialIn, mapColorIn);
		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setTickRandomly(true);
	}

	/**
	 * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess,BlockPos)} whenever possible. Implementing/overriding is fine.
	 */
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		boolean flag = this.getRedstoneStrength(state) > 0;
		return flag ? PRESSED_AABB : UNPRESSED_AABB;
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return 20;
	}

	@Nullable

	/**
	 * @deprecated call via {@link IBlockState#getCollisionBoundingBox(IBlockAccess,BlockPos)} whenever possible. Implementing/overriding is fine.
	 */
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 * 
	 * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/**
	 * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * Determines if an entity can path through this block
	 */
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	/**
	 * Return true if an entity can be spawned inside the block (used to get the player's bed spawn location)
	 */
	public boolean canSpawnInBlock() {
		return true;
	}

	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return this.canBePlacedOn(worldIn, pos.down());
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid block, etc.
	 */
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canBePlacedOn(worldIn, pos.down())) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean canBePlacedOn(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos).isTopSolid() || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
	}

	/**
	 * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
	 */
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			int i = this.getRedstoneStrength(state);

			if (i > 0) {
				this.updateState(worldIn, pos, state, i);
			}
		}
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote) {
			int i = this.getRedstoneStrength(state);

			if (i == 0) {
				this.updateState(worldIn, pos, state, i);
			}
		}
	}

	/**
	 * Updates the pressure plate when stepped on
	 */
	protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
		int i = this.computeRedstoneStrength(worldIn, pos);
		boolean flag = oldRedstoneStrength > 0;
		boolean flag1 = i > 0;

		if (oldRedstoneStrength != i) {
			state = this.setRedstoneStrength(state, i);
			worldIn.setBlockState(pos, state, 2);
			this.updateNeighbors(worldIn, pos);
			worldIn.markBlockRangeForRenderUpdate(pos, pos);
		}

		if (!flag1 && flag) {
			this.playClickOffSound(worldIn, pos);
		} else if (flag1 && !flag) {
			this.playClickOnSound(worldIn, pos);
		}

		if (flag1) {
			worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
		}
	}

	protected abstract void playClickOnSound(World worldIn, BlockPos color);

	protected abstract void playClickOffSound(World worldIn, BlockPos pos);

	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (this.getRedstoneStrength(state) > 0) {
			this.updateNeighbors(worldIn, pos);
		}

		super.breakBlock(worldIn, pos, state);
	}

	/**
	 * Notify block and block below of changes
	 */
	protected void updateNeighbors(World worldIn, BlockPos pos) {
		worldIn.notifyNeighborsOfStateChange(pos, this, false);
		worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
	}

	/**
	 * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible. Implementing/overriding is fine.
	 */
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return this.getRedstoneStrength(blockState);
	}

	/**
	 * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible. Implementing/overriding is fine.
	 */
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP ? this.getRedstoneStrength(blockState) : 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 * 
	 * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	/**
	 * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
	 */
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.DESTROY;
	}

	protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

	protected abstract int getRedstoneStrength(IBlockState state);

	protected abstract IBlockState setRedstoneStrength(IBlockState state, int strength);

	/**
	 * Get the geometry of the queried face at the given position and state. This is used to decide whether things like buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
	 * <p>
	 * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does not fit the other descriptions and will generally cause other things not to connect to the face.
	 * 
	 * @return an approximation of the form of the given face
	 * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible. Implementing/overriding is fine.
	 */
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
