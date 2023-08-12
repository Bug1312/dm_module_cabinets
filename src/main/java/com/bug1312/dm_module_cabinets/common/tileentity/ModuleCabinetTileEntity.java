// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.tileentity;

import java.util.List;

import com.bug1312.dm_module_cabinets.common.init.BlockEntities;
import com.bug1312.dm_module_cabinets.common.init.Blocks;
import com.bug1312.dm_module_cabinets.helpers.CabinetWaypoint;
import com.bug1312.dm_module_cabinets.helpers.TardisHelper;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.tileentity.DMTileEntityBase;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ModuleCabinetTileEntity extends DMTileEntityBase implements ISidedInventory {
	
	public NonNullList<ItemStack> cartridges = NonNullList.withSize(8, ItemStack.EMPTY);
	public Integer selected = null;
	
	public ModuleCabinetTileEntity() {
		super(BlockEntities.MODULE_CABINET.get());
	}
	
	public ITextComponent getNameFromSlot(int slot) {
		return cartridges.get(slot).getHoverName();
	}
	
	public void select(Integer slot) {
		if (level.isClientSide()) return;
		
		selected = slot;
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, (slot != null)));

		if (slot != null) {
			List<ModuleCabinetTileEntity> cabinets = TardisHelper.getTilesInTardis(this, ModuleCabinetTileEntity.class);
			for (ModuleCabinetTileEntity tile:cabinets) {
				if (tile.getBlockPos() != getBlockPos() && tile.selected != null) tile.select(null);
			}
		}
		
		List<ModuleSelectorTileEntity> panels = TardisHelper.getTilesInTardis(this, ModuleSelectorTileEntity.class);
		for (ModuleSelectorTileEntity tile:panels) {
			if (selected == null) tile.updateWaypoint(null, false);
			else tile.updateWaypoint(new CabinetWaypoint(cartridges.get(slot), getBlockPos(), slot), false);
		}
		
		sendUpdates();
	}
	
	public void eject(Integer slot) {
		ItemStack itemstack = cartridges.get(slot);
		
		if (!itemstack.isEmpty()) {
			Direction direction = getBlockState().getValue(HorizontalBlock.FACING);
			ItemEntity entity = new ItemEntity(level, getBlockPos().getX() + 0.7 + direction.getStepX(), getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.7 + direction.getStepZ(), itemstack);
			entity.setDeltaMovement(direction.getStepX() / 10F, 0, direction.getStepZ() / 10F);
			level.addFreshEntity(entity);
		
			cartridges.set(slot, ItemStack.EMPTY);
			level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		}
		
		if (selected == slot) select(null);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.save(nbt);

		return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		cartridges.clear();
		super.load(state, compound);
		
		ItemStackHelper.loadAllItems(compound, cartridges);
		
		if (compound.contains("Selected")) selected = compound.getInt("Selected");
		else selected = null;
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		
		ItemStackHelper.saveAllItems(compound, cartridges);
		
		if (selected != null) compound.putInt("Selected", selected);
		else compound.remove("Selected");
		
		return compound;
	}

	@Override
	public void clearContent() {
		cartridges.clear();
	}

	@Override
	public int getContainerSize() {
		if (getBlockState().getBlock() != Blocks.MODULE_CABINET.get()) return 8;
		return getBlockState().getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 8 : 4;
	}

	@Override
	public boolean isEmpty() {
		return cartridges.stream().allMatch(ItemStack::isEmpty);
	}

	public NonNullList<ItemStack> getItems() {
		return this.cartridges;
	}

	
	@Override
	public ItemStack getItem(int slot) {
		return cartridges.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = ItemStackHelper.removeItem(cartridges, slot, 1);
		if (selected != null && slot == selected) select(null);
		sendUpdates();
		return itemstack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ItemStackHelper.takeItem(cartridges, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {	
		cartridges.set(slot, stack);
		stack.setCount(1);
		sendUpdates();
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
		if (level.getBlockEntity(worldPosition) != this) return false;
		else return !(player.distanceToSqr((double)worldPosition.getX() + 0.5D, (double)worldPosition.getY() + 0.5D, (double)worldPosition.getZ() + 0.5D) > 64.0D);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {	
		switch (getBlockState().getValue(SlabBlock.TYPE)) {
			case DOUBLE: default: return new int[]{0,1,2,3,4,5,6,7};
			case TOP: return new int[] {0,1,2,3};
			case BOTTOM: return new int[] {4,5,6,7};
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		Item item = stack.getItem();
		return (
				(item == DMItems.DATA_MODULE.get() || item == DMItems.DATA_MODULE_GOLD.get()) &&
				cartridges.get(slot).isEmpty()
		);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return true;
	}

}
