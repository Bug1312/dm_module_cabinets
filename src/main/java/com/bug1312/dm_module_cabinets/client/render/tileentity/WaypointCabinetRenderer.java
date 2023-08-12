// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.client.render.tileentity;

import com.bug1312.dm_module_cabinets.ModMain;
import com.bug1312.dm_module_cabinets.common.tileentity.ModuleCabinetTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.swdteam.common.init.DMItems;
import com.swdteam.model.javajson.JSONModel;
import com.swdteam.model.javajson.ModelLoader;
import com.swdteam.model.javajson.ModelWrapper;

import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class WaypointCabinetRenderer extends TileEntityRenderer<ModuleCabinetTileEntity> {

	public static JSONModel MODEL = ModelLoader.loadModel(new ResourceLocation(ModMain.MOD_ID, "models/tileentity/module_cabinet.json"));
	private JSONModel model;
	
	public WaypointCabinetRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
		model = MODEL;
	}

	@Override
	public void render(ModuleCabinetTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {	
		matrixStack.pushPose();
		try {			
			matrixStack.translate(0.5d, 0.0d, 0.5d);
			matrixStack.translate(0, 1.5d, 0d);
			matrixStack.translate(0, -1.5f, 0);
			float scale = model.getModelData().getModel().modelScale;
			matrixStack.scale(scale,scale,scale);
			matrixStack.translate(0, 1.5f, 0);
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
			
			IVertexBuilder ivertexbuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutoutNoCull(MODEL.getModelData().getTexture()));//rendermaterial.buffer(iRenderTypeBuffer, RenderType::entitySolid);
			ModelWrapper wrapper = model.getModelData().getModel();

			boolean isPowered = tile.getBlockState().getValue(BlockStateProperties.POWERED);
			float rotation = 0;
			switch (tile.getBlockState().getValue(HorizontalBlock.FACING)) {
				case NORTH: default: 	rotation = 0; break;
				case EAST: 				rotation = 90; break;
				case SOUTH: 			rotation = 180; break;
				case WEST: 				rotation = 270; break;
			}	
			
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

			ModelRenderer slab = new ModelRenderer(0, 0, 0, 0);
			slab.addChild(wrapper.getPart("slab"));
			
	        switch (tile.getBlockState().getValue(SlabBlock.TYPE)) {
				case BOTTOM:
					if (isPowered) slab.addChild(wrapper.getPart("top_indicator"));
					slab.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
					break;
				case DOUBLE:
					ModelRenderer extraSlab = new ModelRenderer(0, 0, 0, 0);
					extraSlab.addChild(wrapper.getPart("slab"));
					if (isPowered) extraSlab.addChild(wrapper.getPart("top_indicator"));
					extraSlab.y -= 8;
					extraSlab.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
					
					
					slab.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
					break;
				case TOP:
					if (isPowered) slab.addChild(wrapper.getPart("top_indicator"));
					slab.y -= 8;
					slab.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
					break;
	        }
	        	        
	        for (int i = 0; i < tile.getItems().size(); i++) {
	        	if (tile.getItems().get(i).isEmpty()) continue;
	        	Item item = tile.getItems().get(i).getItem();
	        	
	        	int row = (int) Math.floor(i / 2D);
	        	int col = i % 2;
	        		        	
				ModelRenderer module = new ModelRenderer(0, 0, 0, 0);
				module.y += row * 3 + (row > 1 ? 2 : 0) - 11;
				module.x += col * 7;
				
	        	if (item == DMItems.DATA_MODULE.get()) module.addChild(wrapper.getPart("module"));
	        	if (item == DMItems.DATA_MODULE_GOLD.get()) module.addChild(wrapper.getPart("module_gold"));
				
				module.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        	}
	        
	        if (isPowered) {
				ModelRenderer indicator = new ModelRenderer(0, 0, 0, 0);
				int selected = tile.selected;
	        	int row = (int) Math.floor(selected / 2D);
	        	int col = selected % 2;

	        	indicator.y += row * 3 + (row > 1 ? 2 : 0) - 11;
	        	indicator.x += col * 7;
	        	
	        	indicator.addChild(wrapper.getPart("indicator"));
	        	indicator.render(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
	        }
	        
		} catch(Exception err) {}		
		matrixStack.popPose();
	}
	
}
