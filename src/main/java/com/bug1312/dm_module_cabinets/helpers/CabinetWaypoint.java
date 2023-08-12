// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class CabinetWaypoint {
	public ItemStack stack;
	public BlockPos pos;
	public int slot;
	
	public CabinetWaypoint(ItemStack stack, BlockPos pos, int slot) {
		this.stack = stack;
		this.pos = pos;
		this.slot = slot;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CabinetWaypoint)) return false;
		CabinetWaypoint waypoint = (CabinetWaypoint) obj;
		return pos.equals(waypoint.pos) && slot == waypoint.slot;
	}
}
