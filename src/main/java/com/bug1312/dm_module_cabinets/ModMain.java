// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets;

import com.bug1312.dm_module_cabinets.client.render.tileentity.ModuleSelectorPanelRenderer;
import com.bug1312.dm_module_cabinets.client.render.tileentity.WaypointCabinetRenderer;
import com.bug1312.dm_module_cabinets.common.RegistryHandler;
import com.bug1312.dm_module_cabinets.common.init.BlockEntities;
import com.bug1312.dm_module_cabinets.common.init.Blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_module_cabinets";

	public ModMain() {
		RegistryHandler.init();
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
	}
	
	public void client(final FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(BlockEntities.MODULE_CABINET.get(), WaypointCabinetRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BlockEntities.MODULE_SELECTOR.get(), ModuleSelectorPanelRenderer::new);
	
		RenderTypeLookup.setRenderLayer(Blocks.MODULE_SELECTOR.get(), RenderType.cutout());
	}
}


