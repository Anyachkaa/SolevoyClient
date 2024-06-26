package net.minecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
	protected static final AxisAlignedBB REED_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

	protected BlockReed() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
		this.setTickRandomly(true);
	}

	/**
	 * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess,BlockPos)} whenever possible. Implementing/overriding is fine.
	 */
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return REED_AABB;
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.getBlockState(pos.down()).getBlock() == Blocks.REEDS || this.checkForDrop(worldIn, pos, state)) {
			if (worldIn.isAirBlock(pos.up())) {
				int i;

				for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
					;
				}

				if (i < 3) {
					int j = ((Integer) state.getValue(AGE)).intValue();

					if (j == 15) {
						worldIn.setBlockState(pos.up(), this.getDefaultState());
						worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 4);
					} else {
						worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.down()).getBlock();

		if (block == this) {
			return true;
		} else if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND) {
			return false;
		} else {
			BlockPos blockpos = pos.down();

			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));

				if (iblockstate.getMaterial() == Material.WATER || iblockstate.getBlock() == Blocks.FROSTED_ICE) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid block, etc.
	 */
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkForDrop(worldIn, pos, state);
	}

	protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
		if (this.canBlockStay(worldIn, pos)) {
			return true;
		} else {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return false;
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos) {
		return this.canPlaceBlockAt(worldIn, pos);
	}

	@Nullable

	/**
	 * @deprecated call via {@link IBlockState#getCollisionBoundingBox(IBlockAccess,BlockPos)} whenever possible. Implementing/overriding is fine.
	 */
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.REEDS;
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

	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(Items.REEDS);
	}

	/**
	 * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
	 */
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(AGE)).intValue();
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AGE });
	}

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
