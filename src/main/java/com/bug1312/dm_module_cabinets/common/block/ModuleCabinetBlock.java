// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.bug1312.dm_module_cabinets.common.tileentity.ModuleCabinetTileEntity;
import com.swdteam.common.block.IBlockTooltip;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ModuleCabinetBlock extends SlabBlock implements IBlockTooltip {

	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public static List<WaypointPanelSlots> buttons = new ArrayList<WaypointPanelSlots>();
	
	public ModuleCabinetBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(super.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}

	@Override	
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState state2, boolean bool) {
		if (!state.is(state2.getBlock())) {
			TileEntity tile = world.getBlockEntity(pos);
			if (tile instanceof ModuleCabinetTileEntity) {
				InventoryHelper.dropContents(world, pos, (ModuleCabinetTileEntity) tile);
				world.updateNeighbourForOutputSignal(pos, this);
			}
		}

		super.onRemove(state, world, pos, state2, bool);
	}
	
	@Override @SuppressWarnings("deprecation")
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof ModuleCabinetTileEntity) {
			ModuleCabinetTileEntity tile = (ModuleCabinetTileEntity) te;
			WaypointPanelSlots button = getButtonFromMouse(state, pos, result.getLocation());
			if (button == null) return super.use(state, world, pos, player, hand, result);

			ItemStack slotStack = tile.cartridges.get(button.ordinal());
			
			if (!slotStack.isEmpty()) {
				if (player.isShiftKeyDown()) {
					tile.eject(button.ordinal());
				} else {
					tile.select(button.ordinal());
					world.playSound(null, pos, DMSoundEvents.TARDIS_CONTROLS_BUTTON_CLICK.get(), SoundCategory.BLOCKS, 1, 1);
				}
			} else {
				ItemStack heldStack = player.getMainHandItem();
				Item item = heldStack.getItem();
				if (item == DMItems.DATA_MODULE.get() || item == DMItems.DATA_MODULE_GOLD.get()) {
					ItemStack insertStack = heldStack.copy();
					insertStack.setCount(1);
					tile.cartridges.set(button.ordinal(), insertStack);
					world.updateNeighbourForOutputSignal(pos, this);
					if (!player.abilities.instabuild) heldStack.shrink(1);
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), DMSoundEvents.TARDIS_MODULE_INSERT.get(), SoundCategory.BLOCKS, 1, 1);
				}
			}
		}
		return ActionResultType.CONSUME;
	}
	
	@Override
	public ITextComponent getName(BlockState state, BlockPos pos, Vector3d vector, PlayerEntity player) {
		WaypointPanelSlots button = getButtonFromMouse(state, pos, vector);
		if (button == null) return null;
		
		TileEntity te = player.level.getBlockEntity(pos);
		if (te instanceof ModuleCabinetTileEntity) {
			ModuleCabinetTileEntity tile = (ModuleCabinetTileEntity) te;
			if (!tile.cartridges.get(button.ordinal()).isEmpty()) {
				if (player.isShiftKeyDown()) return new TranslationTextComponent("tooltip.dm_module_cabinets.module_cabinet.eject");
				return tile.getNameFromSlot(button.ordinal());
			}
			return new TranslationTextComponent("tooltip.dm_module_cabinets.module_cabinet.load");
		}
		
		return null;
	}

	@Nullable
	private WaypointPanelSlots getButtonFromMouse(BlockState state, BlockPos pos, Vector3d mouse) {
		double mouseLon;
		double mouseLat;
		double mouseY = 1 - (mouse.y() - pos.getY());

		switch (state.getValue(FACING)) {
		    case NORTH: default:
		    	mouseLat = 1 - (mouse.x() - pos.getX());
		        mouseLon = (mouse.z() - pos.getZ());
		        break;
		    case EAST:
		    	mouseLat = 1 - (mouse.z() - pos.getZ());
		        mouseLon = 1 - (mouse.x() - pos.getX());
		        break;
	        case SOUTH:
		    	mouseLat = (mouse.x() - pos.getX());
		        mouseLon = 1 - (mouse.z() - pos.getZ());
		        break;
		    case WEST:
		    	mouseLat = (mouse.z() - pos.getZ());
		        mouseLon = (mouse.x() - pos.getX());
		        break;
		}
		
		for (WaypointPanelSlots button : WaypointPanelSlots.values())
			if (
					mouseLat >= button.x && mouseLat <= button.x + button.width && 
					mouseY >= button.y && mouseY <= button.y + button.height && 
					mouseLon >= button.z && mouseLon <= button.z + button.depth
				) return button;

		return null;
	}
		
	private static enum WaypointPanelSlots {
		SLOT_1(8.5,12.5,-2,6,2.5,3),
		SLOT_2(1.5,12.5,-2,6,2.5,3),
		SLOT_3(8.5,9.5,-2,6,2.5,3),
		SLOT_4(1.5,9.5,-2,6,2.5,3),
		SLOT_5(8.5,4.5,-2,6,2.5,3),
		SLOT_6(1.5,4.5,-2,6,2.5,3),
		SLOT_7(8.5,1.5,-2,6,2.5,3),
		SLOT_8(1.5,1.5,-2,6,2.5,3);
				
		double x, y, z, width, height, depth;

		// Use BlockBench values facing North in JSON Block
		WaypointPanelSlots(double posX, double posY, double posZ, double width, double height, double depth) {
			float f = 1.0F / 16.0F; // 0.0625
			
			this.x = 1 - (posX + width) * f;
			this.y = 1 - (posY + height) * f;
			this.z = posZ * f;	
			
			this.width = width * f;
			this.height = height * f;
			this.depth = depth * f;

			
			buttons.add(this);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof ModuleCabinetTileEntity) {
			ModuleCabinetTileEntity cabinet = ((ModuleCabinetTileEntity) tile);
			return cabinet.cartridges.stream().filter(stack -> !stack.isEmpty()).toArray().length * 2;
		}
		
		return 0;
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
		builder.add(POWERED);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ModuleCabinetTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
}
