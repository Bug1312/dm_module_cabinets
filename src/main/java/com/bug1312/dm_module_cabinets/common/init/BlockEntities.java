// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.init;

import com.bug1312.dm_module_cabinets.ModMain;
import com.bug1312.dm_module_cabinets.common.tileentity.ModuleCabinetTileEntity;
import com.bug1312.dm_module_cabinets.common.tileentity.ModuleSelectorTileEntity;
import com.swdteam.common.RegistryHandler;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockEntities {
	public static final RegistryObject<TileEntityType<ModuleCabinetTileEntity>> MODULE_CABINET = RegistryHandler.TILE_ENTITY_TYPES.register("module_cabinet", () -> TileEntityType.Builder.of(ModuleCabinetTileEntity::new, Blocks.MODULE_CABINET.get()).build(null));
	public static final RegistryObject<TileEntityType<ModuleSelectorTileEntity>> MODULE_SELECTOR = RegistryHandler.TILE_ENTITY_TYPES.register("wireless_module_selector", () -> TileEntityType.Builder.of(ModuleSelectorTileEntity::new, Blocks.MODULE_SELECTOR.get()).build(null));
}
