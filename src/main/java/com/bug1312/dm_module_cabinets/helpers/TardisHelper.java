// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bug1312.dm_module_cabinets.common.tileentity.ModuleCabinetTileEntity;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

public class TardisHelper {

	public static <T extends TileEntity> List<T> getTilesInTardis(int id, World world, Class<T> tileClazz) {
		TardisData data = DMTardis.getTardis(id);
		ArrayList<T> output = new ArrayList<>();
		if (world.dimension() == DMDimensions.TARDIS && data != null && data.hasGenerated()) {
			BlockPos start = new BlockPos(DMTardis.getXZForMap(id).getX() * 256, 0, DMTardis.getXZForMap(id).getZ() * 256);
						
		    BlockPos.Mutable mutablePos = new BlockPos.Mutable();
		    
		    // From start of TARDIS to end of TARDIS in chunk's size steps
		    for (int x = start.getX(); x < start.getX() + 256; x += 16) {
	            for (int z = start.getZ(); z < start.getZ() + 256; z += 16) {
	                mutablePos.set(x, 0, z);
	                IChunk chunk = world.getChunk(mutablePos);
	                Set<BlockPos> blockPos = chunk.getBlockEntitiesPos();
	                blockPos.forEach(pos -> {
		                TileEntity tile = world.getBlockEntity(pos);
		                if (tile != null && tileClazz.isInstance(tile)) output.add(tileClazz.cast(tile));
	                });
	            }
		    }
		} 
		return output;
	}
	
	public static <T extends TileEntity> List<T> getTilesInTardis(TileEntity tile, Class<T> tileClazz) {
		return TardisHelper.getTilesInTardis(DMTardis.getIDForXZ(tile.getBlockPos().getX(), tile.getBlockPos().getZ()), tile.getLevel(), tileClazz);
	}
	
	public static List<CabinetWaypoint> getAllStoredWaypoints(int id, World world) {
		ArrayList<CabinetWaypoint> output = new ArrayList<>();
		
		List<ModuleCabinetTileEntity> cabinets = getTilesInTardis(id, world, ModuleCabinetTileEntity.class);
		for (ModuleCabinetTileEntity tile: cabinets) {
			for (int i = 0; i < tile.cartridges.size(); i++) {
				ItemStack cartridge = tile.cartridges.get(i);
				if (cartridge == null || cartridge.isEmpty() || !cartridge.hasTag() || !cartridge.getTag().getBoolean("written")) continue;
				output.add(new CabinetWaypoint(cartridge, tile.getBlockPos(), i));
			}
		}

		return output;
	}
	
	public static List<CabinetWaypoint> getAllStoredWaypoints(TileEntity tile) {
		return TardisHelper.getAllStoredWaypoints(DMTardis.getIDForXZ(tile.getBlockPos().getX(), tile.getBlockPos().getZ()), tile.getLevel());
	}


	
}
