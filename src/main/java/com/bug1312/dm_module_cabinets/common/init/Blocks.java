// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.common.init;

import com.bug1312.dm_module_cabinets.common.RegistryHandler;
import com.bug1312.dm_module_cabinets.common.block.ModuleCabinetBlock;
import com.bug1312.dm_module_cabinets.common.block.ModuleSelectorBlock;
import com.bug1312.dm_module_cabinets.common.tileentity.ModuleSelectorTileEntity;
import com.google.common.base.Supplier;
import com.swdteam.common.init.DMTabs;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

public class Blocks {
	public static final RegistryObject<Block> MODULE_CABINET = registerBlockAndItem("module_cabinet", () -> new ModuleCabinetBlock(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(6F, 6F).dynamicShape().noOcclusion().requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(1).sound(SoundType.METAL)), new Item.Properties().tab(DMTabs.DM_TARDIS));
	public static final RegistryObject<Block> MODULE_SELECTOR = registerBlockAndItem("wireless_module_selector", () -> new ModuleSelectorBlock(ModuleSelectorTileEntity::new, Properties.of(Material.STONE).instabreak().noOcclusion().sound(SoundType.WOOD)), new Item.Properties().tab(DMTabs.DM_TARDIS));

	/* Register Method */
	public static <B extends Block> RegistryObject<Block> registerBlockAndItem(String name, Supplier<B> block, Item.Properties properties) {
		RegistryObject<Block> blockObject = RegistryHandler.BLOCKS.register(name, block);
		RegistryHandler.ITEMS.register(name, () -> new BlockItem(blockObject.get(), properties));

		return blockObject;
	}

}