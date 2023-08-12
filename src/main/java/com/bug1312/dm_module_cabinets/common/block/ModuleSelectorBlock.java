// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.bug1312.dm_module_cabinets.common.tileentity.ModuleSelectorTileEntity;
import com.swdteam.common.block.IBlockTooltip;
import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.init.DMSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ModuleSelectorBlock extends RotatableTileEntityBase.WaterLoggable implements IBlockTooltip {

	protected static final VoxelShape SHAPE_N = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(0, 2/16D, 6/16D, 1, 8/16D, 1), VoxelShapes.box(7/16D, 8/16D, 10/16D, 9/16D, 15/16D, 12/16D), VoxelShapes.box(1/16D, 2/16D, 1/16D, 5/16D, 3/16D, 4/16D), VoxelShapes.box(11/16D, 2/16D, 1/16D, 15/16D, 3/16D, 4/16D), VoxelShapes.box(6/16D, 2/16D, 1/16D, 10/16D, 3/16D, 4/16D));
	protected static final VoxelShape SHAPE_E = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(0, 2/16D, 0, 10/16D, 8/16D, 1), VoxelShapes.box(4/16D, 8/16D, 7/16D, 6/16D, 15/16D, 9/16D), VoxelShapes.box(12/16D, 2/16D, 1/16D, 15/16D, 3/16D, 5/16D), VoxelShapes.box(12/16D, 2/16D, 11/16D, 15/16D, 3/16D, 15/16D), VoxelShapes.box(12/16D, 2/16D, 6/16D, 15/16D, 3/16D, 10/16D));
	protected static final VoxelShape SHAPE_S = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(0, 2/16D, 0, 1, 8/16D, 10/16D), VoxelShapes.box(7/16D, 8/16D, 4/16D, 9/16D, 15/16D, 6/16D), VoxelShapes.box(11/16D, 2/16D, 12/16D, 15/16D, 3/16D, 15/16D), VoxelShapes.box(1/16D, 2/16D, 12/16D, 5/16D, 3/16D, 15/16D), VoxelShapes.box(6/16D, 2/16D, 12/16D, 10/16D, 3/16D, 15/16D));
	protected static final VoxelShape SHAPE_W = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(6/16D, 2/16D, 0, 1, 8/16D, 1), VoxelShapes.box(10/16D, 8/16D, 7/16D, 12/16D, 15/16D, 9/16D), VoxelShapes.box(1/16D, 2/16D, 11/16D, 4/16D, 3/16D, 15/16D), VoxelShapes.box(1/16D, 2/16D, 1/16D, 4/16D, 3/16D, 5/16D), VoxelShapes.box(1/16D, 2/16D, 6/16D, 4/16D, 3/16D, 10/16D));

	public static final IntegerProperty BUTTON_SELECTED = IntegerProperty.create("button_pressed", 0, 3);

	public static List<ModuleSelectorButtons> buttons = new ArrayList<ModuleSelectorButtons>();

	public ModuleSelectorBlock(Supplier<TileEntity> tileEntitySupplier, Properties properties) {
		super(tileEntitySupplier, properties);
		this.registerDefaultState(super.defaultBlockState().setValue(BUTTON_SELECTED, 0));
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BUTTON_SELECTED);
	}
		
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(FACING);
		switch (facing) {
			case NORTH: default: return SHAPE_N;
			case EAST: return SHAPE_E;
			case SOUTH: return SHAPE_S;
			case WEST: return SHAPE_W;
		}
	}
	
	@Override
	public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2) {
		return direction == Direction.DOWN && !this.canSurvive(state2, world, pos1) ? Blocks.AIR.defaultBlockState() : super.updateShape(state1, direction, state2, world, pos1, pos2);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return canSupportCenter(world, pos.below(), Direction.UP);
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
		if (!world.isClientSide && hand == Hand.MAIN_HAND) {
			Vector3d mouseVector = rayTraceResult.getLocation();
			double mouseX = mouseVector.x() - pos.getX();
			double mouseZ = mouseVector.z() - pos.getZ();
			ModuleSelectorButtons button = getButtonFromMouse(mouseX, mouseZ, state.getValue(FACING));
			if (state.getValue(BUTTON_SELECTED) != 0) return ActionResultType.CONSUME;
				
			if (button != null) {
				TileEntity tile = world.getBlockEntity(pos);
				if (tile instanceof ModuleSelectorTileEntity) {
					ModuleSelectorTileEntity panel = ((ModuleSelectorTileEntity) tile);

					switch (button) {
						case LEFT:
							panel.shiftSelection(false);
							break;
						case RIGHT:
							panel.shiftSelection(true);
							break;
						case SELECT:
							panel.selectWaypoint();
							break;
					}
					
					world.setBlockAndUpdate(pos, state.setValue(BUTTON_SELECTED, button.ordinal() + 1));
					world.playSound(null, pos, DMSoundEvents.TARDIS_CONTROLS_BUTTON_CLICK.get(), SoundCategory.BLOCKS, 1, 1);
					world.getBlockTicks().scheduleTick(pos, this, 5);
				}
			}			
		}
		return ActionResultType.CONSUME;

	}
	
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		world.setBlockAndUpdate(pos, state.setValue(BUTTON_SELECTED, 0));
		world.playSound(null, pos, DMSoundEvents.TARDIS_CONTROLS_BUTTON_RELEASE.get(), SoundCategory.BLOCKS, 1, 1);

		super.tick(state, world, pos, rand);
	}

	@Override
	public ITextComponent getName(BlockState state, BlockPos pos, Vector3d vector, PlayerEntity player) {
		double mouseX = vector.x() - pos.getX();
		double mouseZ = vector.z() - pos.getZ();

		ModuleSelectorButtons button = getButtonFromMouse(mouseX, mouseZ, state.getValue(FACING));
		if (button == null) return null;
		
		switch (button) {
			case LEFT: return new TranslationTextComponent("tooltip.dm_module_cabinets.wireless_module_selector.left");
			case SELECT: return new TranslationTextComponent("tooltip.dm_module_cabinets.wireless_module_selector.apply");
			case RIGHT: return new TranslationTextComponent("tooltip.dm_module_cabinets.wireless_module_selector.right");
			default: return null;
		}

	}

	@Nullable
	private ModuleSelectorButtons getButtonFromMouse(double mouseX, double mouseZ, Direction facing) {
		for (ModuleSelectorButtons button : ModuleSelectorButtons.values()) {
			if (button.areas.containsKey(facing)) {
				Vector3d vector = button.areas.get(facing);

				float width = button.width;
				float height = button.height;
				double x = vector.x;
				double z = vector.z;

				if ( 	
						(facing == Direction.NORTH && mouseX <= x && mouseZ <= z && mouseX >= x - width && mouseZ >= z - height) ||
						(facing == Direction.SOUTH && mouseX >= x && mouseZ >= z && mouseX <= x + width && mouseZ <= z + height) ||
						(facing == Direction.EAST && mouseX >= x && mouseX <= x + height && mouseZ <= z && mouseZ >= z - width) ||
						(facing == Direction.WEST && mouseX <= x && mouseX >= x - height && mouseZ >= z && mouseZ <= z + width)
					) return button;
			}
		}
		
		return null;
	}

	private static enum ModuleSelectorButtons {
		LEFT(4, 3, 1, 12),
		SELECT(4, 3, 6, 12),
		RIGHT(4, 3, 11, 12);
		
		Map<Direction, Vector3d> areas = new HashMap<Direction, Vector3d>();
		float width, height;

		ModuleSelectorButtons(int width, int height, float left, float top) {
			float f = 1.0F / 16.0F;
			
			float invertLeft = 16 - left;
			float invertTop = 16 - top;
			
			this.width = width * f;
			this.height = height * f;
			
			areas.put(Direction.NORTH, new Vector3d(invertLeft * f, 0, invertTop  * f));
			areas.put(Direction.EAST,  new Vector3d(top        * f, 0, invertLeft * f));
			areas.put(Direction.SOUTH, new Vector3d(left       * f, 0, top        * f));
			areas.put(Direction.WEST,  new Vector3d(invertTop  * f, 0, left       * f));

			buttons.add(this);
		}
	}

}
