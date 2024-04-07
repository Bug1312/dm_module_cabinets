// Copyright 2024 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.tileentity;

import java.util.List;

import com.bug1312.dm_module_cabinets.common.init.BlockEntities;
import com.bug1312.dm_module_cabinets.helpers.CabinetWaypoint;
import com.bug1312.dm_module_cabinets.helpers.TardisHelper;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.tardis.TardisPanelTileEntity;
import com.swdteam.util.SWDMathUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ModuleSelectorTileEntity extends TardisPanelTileEntity {

	private static final long serialVersionUID = -8511035829776989932L;
	public CabinetWaypoint waypoint;
	public ITextComponent selected;
	
	public ModuleSelectorTileEntity() {
		super(BlockEntities.MODULE_SELECTOR.get());
	}

	private List<CabinetWaypoint> getWaypoints() {
		return TardisHelper.getAllStoredWaypoints(this);
	}
	
	public void selectWaypoint() {
		if (level.isClientSide() || level.dimension() != DMDimensions.TARDIS || waypoint == null) return;
		
		TardisData data = DMTardis.getTardis(DMTardis.getIDForXZ(getBlockPos().getX(), getBlockPos().getZ()));
		if (data == null) return;
		
		if (this.getDamagePercentage() >= 90 && SWDMathUtils.RANDOM.nextInt(10) == 0) this.shiftSelection(SWDMathUtils.RANDOM.nextBoolean());
		if (!waypoint.stack.hasTag() || !waypoint.stack.getTag().contains("location")) return;

		CompoundNBT tag = waypoint.stack.getTag().getList("location", 10).getCompound(0);
		if (!tag.contains("pos_x")) return;
		
		TardisFlightPool.updateFlight(data, tag.getInt("pos_x"), tag.getInt("pos_y"), tag.getInt("pos_z"), RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("location"))));
	}
	
	public void shiftSelection(boolean right) {
		if (level.isClientSide || level.dimension() != DMDimensions.TARDIS) return;
		
		List<CabinetWaypoint> waypoints = getWaypoints();
		if (waypoints.size() == 0) {
			updateWaypoint(null, false);
			waypoint = null;
			selected = null;
		} else if (waypoints.size() == 1) {
			updateWaypoint(waypoints.get(0), true);
		} else {
			int index = waypoints.indexOf(waypoint);
			index += (right) ? 1 : -1;
			
			if (index < 0) updateWaypoint(waypoints.get(waypoints.size() - 1), true);
			else if (index >= waypoints.size()) updateWaypoint(waypoints.get(0), true);
			else updateWaypoint(waypoints.get(index), true);
		}
	}
	
	public void updateWaypoint(CabinetWaypoint waypoint, boolean updateCabinets) {
		this.waypoint = waypoint;
		
		if (waypoint == null) selected = null;
		else selected = (waypoint.stack.hasTag() && waypoint.stack.getTag().contains("cart_name")) ? new StringTextComponent(waypoint.stack.getTag().getString("cart_name")) : waypoint.stack.getHoverName();
		
		if (updateCabinets) setCabinetStatus();
		
		sendUpdates();
	}
	
	private void setCabinetStatus() {
		int index = getWaypoints().indexOf(waypoint);
		if (index == -1) return;
		
		TileEntity tile = level.getBlockEntity(waypoint.pos);
		if (tile instanceof ModuleCabinetTileEntity) {
			ModuleCabinetTileEntity cabinet = (ModuleCabinetTileEntity) tile;
			cabinet.select(waypoint.slot);
		}
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		selected = (compound.contains("Selected")) ? ITextComponent.Serializer.fromJson(compound.getString("Selected")) : null;
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		
		if (selected != null) compound.putString("Selected", ITextComponent.Serializer.toJson(selected));
		
		return compound;
	}
	
	@Override
	public ResourceLocation getGUIIcon() {
		return new ResourceLocation("dm_module_cabinets:textures/item/wireless_module_selector.png");
	}
}
