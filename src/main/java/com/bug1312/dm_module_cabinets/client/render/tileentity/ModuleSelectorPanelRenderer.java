// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_module_cabinets.client.render.tileentity;

import com.bug1312.dm_module_cabinets.common.tileentity.ModuleSelectorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class ModuleSelectorPanelRenderer extends TileEntityRenderer<ModuleSelectorTileEntity> {

	public ModuleSelectorPanelRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(ModuleSelectorTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
		FontRenderer font = this.renderer.getFont();
		BlockState blockstate = tile.getBlockState();
		matrixStack.pushPose();

		matrixStack.translate(0.5D, 0.5D, 0.5D);
		float rotation = -blockstate.getValue(HorizontalBlock.FACING).toYRot();
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
		matrixStack.translate(-0.40625D, -0.125D, 0.13125D);
		matrixStack.scale(0.01F, -0.01F, 0.01F);
		
		if (tile.selected != null) {
			String text = tile.selected.getString();
			if (font.width(text) >= 70) text = font.plainSubstrByWidth(text, 75) + "..";
			font.draw(matrixStack, text, 0, 0, 0xffffffff);
		}

		matrixStack.popPose();
	}
}
